package MultipleTypesAgentsExamples;

import java.awt.Color;

import sim.engine.SimState;
import sim.engine.Stoppable;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.Bag;

public class Agent extends Particle{
	public int sexID; //An agent's biological sex
	public double attractiveness; //arbitrary attractivenes
	public double maxAttractiveness;//maximum attractiveness of agents
	public OvalPortrayal2D myPortrayal2D;//portrayal of different agents
	public int dateNumber = 0;//number of dates that changes during a simulation
	public int maxDates; //maximum number of dates.
	public double r; //choosiness
	public boolean chooseTheBest;//choose the best = true
	public Stoppable event = null;//to get rid of agents
	PearsonCorrelation PC;//probe to calculate a correlation
	public int dateSearchRadius;//Specify search area for mates


	public void setPC(PearsonCorrelation pC) {
		PC = pC;
	}

	public void setDateSearchRadius(int dateSearchRadius) {
		this.dateSearchRadius = dateSearchRadius;
	}

	public void setChooseTheBest(boolean chooseTheBest) {
		this.chooseTheBest = chooseTheBest;
	}

	public void setR(double r) {
		this.r = r;
	}

	public void setMaxDates(int maxDates) {
		this.maxDates = maxDates;
	}

	public void setSexID(int sexID) {
		this.sexID = sexID;
	}

	public void setAttractiveness(double attractiveness) {
		this.attractiveness = attractiveness;
	}

	public void setMaxAttractiveness(double maxAttractiveness) {
		this.maxAttractiveness = maxAttractiveness;
	}

	public OvalPortrayal2D setColor(SimState state){
		MateChoiceEnvironment e = (MateChoiceEnvironment)state;
		OvalPortrayal2D o = null;
		if(sexID == e.MALE){//the agent is a male
			Color c = new Color((float)0,(float)0,(float)1,(float)
					((float)this.attractiveness/(float)maxAttractiveness));
			o = new OvalPortrayal2D(c);
			e.particlesPortrayal.setPortrayalForObject(this, o);   
		}
		else{
			Color c = new Color((float)1,(float)0,(float)0,(float)
					((float)this.attractiveness/(float)maxAttractiveness));
			o = new OvalPortrayal2D(c);
			e.particlesPortrayal.setPortrayalForObject(this, o);    
		}
		return o;
	}

	public double chooseBest(Agent other){
		double closingTime = 0;//A closing time parameter
		if(dateNumber <= maxDates){
			closingTime = ((double)maxDates -(double)dateNumber)/(double)maxDates; 
		}          
		else {
			closingTime = 0;
		}          
		double pBest = Math.pow(other.attractiveness/maxAttractiveness,  
				r*closingTime);    //kalick and hamilton's probability of choosing
		//another individual with a given attractiveness
		if(pBest > 1){//check to make sure the result above is a probability
			pBest = 1;
		}
		else if(pBest < 0){
			pBest = 0;
		}
		return pBest;
	}

	public double chooseSimilar(Agent agent1, Agent agent2){
		double closingTime = 0;
		if(dateNumber <= maxDates){//calculate the closing time parameter
			closingTime = ((double)maxDates -(double)dateNumber)/(double)maxDates;
		} 
		else {
			closingTime = 0;
		}   
		double pSimilar = Math.pow((maxAttractiveness - 
				Math.abs(agent2.attractiveness - 
						agent1.attractiveness))/maxAttractiveness,  
						r*closingTime); //probability of choosing an agent based
		//on attractiveness similarity
		if(pSimilar > 1){//Make sure it is a probability
			pSimilar = 1;
		}
		else if(pSimilar < 0){
			pSimilar = 0;
		}
		return pSimilar;
	}

	public void removeAgent(SimState state, Agent a){//Our method for removing agents
		//from the simulation
		MateChoiceEnvironment e = (MateChoiceEnvironment)state;
		a.event.stop();
		e.particleSpace.remove(a);
	}

	//The method below is from the lab page.
	public void addAgent(SimState state, int sex){
		MateChoiceEnvironment e = (MateChoiceEnvironment)state;
		if(sex == e.FEMALE)
			e.placeAgent(sex, e.femalePop, PC );
		else
			e.placeAgent(sex, e.malePop, PC );
	}

	public void kh_model(SimState state){
		MateChoiceEnvironment e = (MateChoiceEnvironment)state;
		Agent f, m;
		boolean match = false; //assume a failed match

		if(this.sexID == MateChoiceEnvironment.FEMALE){
			if(e.malePop.numObjs>0)
				m = (Agent)e.malePop.get(e.random.nextInt(e.malePop.numObjs));//get a random male
			else  //choose a random male
				m=null;
			f = this;
		}
		else {
			if(e.femalePop.numObjs>0)
				f = (Agent)e.femalePop.get(e.random.nextInt(e.femalePop.numObjs));//get a random female
			else
				f=null;
			m = this;
		}

		if(chooseTheBest && (f != null && m != null)){
			match = e.random.nextBoolean(chooseBest(f)) && e.random.nextBoolean(chooseBest(m));
			//If both male and female choose each other, match = true, else false
		}
		else if(f != null && m != null) {//choose similar
			match = e.random.nextBoolean(chooseSimilar(f,m)) && e.random.nextBoolean(chooseSimilar(m,f));
			//If both male and female choose based on similarity of attractivenss
		}

		if(match){
			PC.getData(f.attractiveness, m.attractiveness); //get data
			e.malePop.remove(m);
			m.removeAgent(state,m);
			e.femalePop.remove(f);
			f.removeAgent(state, f);	
			//What else could go here?
		}
		else if(f != null && m != null) { //update only if there was a date successful or not
			f.dateNumber++;
			m.dateNumber++;	
		}
	}

	public Bag sortBySex(SimState state, Bag neighbors, int sexID){
		Bag dates = new Bag();
		for(int i=0;i<neighbors.numObjs;i++){
			Agent a = (Agent)neighbors.objs[i];
			if(a.sexID == sexID){
				dates.add(a);
			}
		}
		return dates;
	}

	public void khm_model(SimState state){
		MateChoiceEnvironment e = (MateChoiceEnvironment)state;
		Agent f, m;
		boolean match = false; //assume a failed match
		if(this.sexID == MateChoiceEnvironment.FEMALE){
			Bag neighbors = e.particleSpace.getNeighborsMaxDistance(x, y,
					dateSearchRadius, !boundaries, null, null, null);
			Bag dates = sortBySex(state,neighbors,MateChoiceEnvironment.MALE);
			if(dates.numObjs>0)
				m = (Agent)dates.get(e.random.nextInt(dates.numObjs));//get a random male
			else
				m=null;
			f = this;
		}
		else {//else male
			Bag neighbors = e.particleSpace.getNeighborsMaxDistance(x, y,
					dateSearchRadius, !boundaries, null, null, null);
			Bag dates = sortBySex(state,neighbors,MateChoiceEnvironment.FEMALE);
			if(dates.numObjs>0)
				f = (Agent)dates.get(e.random.nextInt(dates.numObjs));//get a random female
			else
				f = null;
			m = this;
		}

		if(chooseTheBest && (f != null && m != null)){
			match = e.random.nextBoolean(chooseBest(f)) && e.random.nextBoolean(chooseBest(m));
		}
		else if(f != null && m != null) {//choose similar
			match = e.random.nextBoolean(chooseSimilar(f,m)) && e.random.nextBoolean(chooseSimilar(m,f));
		}

		if(match){
			PC.getData(f.attractiveness, m.attractiveness); //get data
			e.malePop.remove(m);
			m.removeAgent(state, m);
			e.femalePop.remove(f);
			f.removeAgent(state, f);
			//What else could go here?
		}
		else if(f != null && m != null) { //update only if there was a date successful or not
			f.dateNumber++;
			m.dateNumber++;	
		}
	}

	public void step(SimState state){
		MateChoiceEnvironment e = (MateChoiceEnvironment)state;
		switch (e.simulationType){//what type of simulation
		case MateChoiceEnvironment.AGGREGATE_COORDINATE:
			super.step(state);//Just the particle model
			break;
		case MateChoiceEnvironment.KH:
			kh_model(state);//Just run the Kalick and Hamilton model
			//without space and movement
			break;
		case MateChoiceEnvironment.KHM://moves in space then searches
			//for a mate
			super.step(state);
			khm_model(state);//extended KH model
			break;
		default:
			kh_model(state);
		}
	}

}
