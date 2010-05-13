
public class MyRaceCondition {
	
	  private static class Pair {
		    String x = "x";
		    String y = "y";
	  
		    public void update() {
		      x = x + y + x;
            }
	  }

	  private static class RC extends Thread {
		    Pair p;

		    public void run() {
		      p.update();
		    }
	  }
	  
	  public static void main(String[] args) throws InterruptedException {
		    Pair p = new Pair();
		    RC rc1 = new RC();
		    RC rc2 = new RC();

		    rc1.p = p;
		    rc2.p = p;

		    rc1.start();
		    rc2.start();
		    rc1.join();
		    rc2.join();
		    System.out.println("x: " + p.x);
     }

}
