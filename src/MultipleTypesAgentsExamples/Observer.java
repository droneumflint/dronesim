package MultipleTypesAgentsExamples;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Bag;
import sim.util.Double2D;

public class Observer implements Steppable{
	Bag malePop;
	Bag femalePop;
	MateChoiceEnvironment se;
	int paired = 0;
	Stoppable event;
	PearsonCorrelation correlation;


	public void step(SimState state) {
		printDataKH();
		testEndKH( state);
	}

	public void printDataKH(){
		if(se.schedule.getTime()<1){ 			
			System.out.println("Time    r     pairs   meanF    meanM"); 		         } 		
		Double2D c = correlation.means(); 		
		String r = new String().format("%.3f",correlation.correlation()); 	
		String meanf = new String().format("%.3f",c.x); 		
		String meanm = new String().format("%.3f",c.y); 	 		
		System.out.println(se.schedule.getTime()+"   "+r+ "    "+ 
				correlation.n+ "     "+ meanf+ "    "+meanm); 	
	} 


	public void testEndKH(SimState state){ 		
		if(malePop.numObjs == 0 || femalePop.numObjs ==0){ 			
			event.stop(); 			
			if(malePop.numObjs > 0){
				for(int k=0; k<malePop.numObjs;k++){ 					                       
					Agent a = (Agent)malePop.objs[k]; 					                            
					a.removeAgent(state, a); 				} 			           } 	
			if(femalePop.numObjs > 0){
				for(int k=0; k<femalePop.numObjs;k++){
					Agent a = (Agent)femalePop.objs[k];
					a.removeAgent(state, a);
				}
			}
		}
	}

	public Observer(MateChoiceEnvironment state, PearsonCorrelation correlation){

		this.malePop = state.malePop;
		this.femalePop = state.malePop;
		event = state.schedule.scheduleRepeating(this, 100, 1);
		se = state;
		this.correlation = correlation;
	}

	public void removeAgent(Agent a){
		a.event.stop();
		se.particleSpace.remove(a);
	}

}
