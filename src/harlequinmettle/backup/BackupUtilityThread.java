package harlequinmettle.backup;

import java.util.Date;

public class BackupUtilityThread extends Thread {

	BackupDefinitionModel modelData;
	public boolean quit = false;

	public BackupUtilityThread(BackupDefinitionModel modelData) { 
		this.modelData = modelData;
	}
	public BackupUtilityThread( ) { 
		this.modelData = new BackupDefinitionModel() ;
		this.modelData.iterations = 1;
	}

	@Override
	public void run() {
		System.out.println("interval for thread: "+modelData.interval);
		for(int i = 0;i<modelData.iterations; i++) {
if(quit )break;
			System.out.println(new Date());
			mapFiles();      
			System.out.println(new Date());
			
			try {
				System.out.println("interval for thread: "+modelData.interval);
				Thread.sleep(modelData.interval * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void mapFiles() {
		System.out.println("mapping files");
modelData.mapFiles();
	}

}
