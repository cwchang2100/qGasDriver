package tw.com.qgas.www.qgasdriver;

import java.util.Locale;

import android.util.Log;

import com.uk.tsl.rfid.ModelBase;
import com.uk.tsl.rfid.asciiprotocol.commands.BarcodeCommand;
import com.uk.tsl.rfid.asciiprotocol.commands.FactoryDefaultsCommand;
import com.uk.tsl.rfid.asciiprotocol.commands.InventoryCommand;
import com.uk.tsl.rfid.asciiprotocol.enumerations.TriState;
import com.uk.tsl.rfid.asciiprotocol.responders.IBarcodeReceivedDelegate;
import com.uk.tsl.rfid.asciiprotocol.responders.ICommandResponseLifecycleDelegate;
import com.uk.tsl.rfid.asciiprotocol.responders.ITransponderReceivedDelegate;
import com.uk.tsl.rfid.asciiprotocol.responders.TransponderData;
import com.uk.tsl.utils.HexEncoding;

public class MainModel extends ModelBase
{

	// Control 
	private boolean mAnyTagSeen;
	private boolean mEnabled;
	public boolean enabled() { return mEnabled; }

	public void setEnabled(boolean state)
	{
		boolean oldState = mEnabled;
		mEnabled = state;

		// Update the commander for state changes
		if (oldState != state) {
			if (mEnabled) {
				// Listen for transponders
				getCommander().addResponder(mInventoryResponder);
				// Listen for barcodes
				getCommander().addResponder(mBarcodeResponder);
			} else {
				// Stop listening for transponders
				getCommander().removeResponder(mInventoryResponder);
				// Stop listening for barcodes
				getCommander().removeResponder(mBarcodeResponder);
			}
			
		}
	}

	// The command to use as a responder to capture incoming inventory responses
	private InventoryCommand mInventoryResponder;
	// The command used to issue commands
	private InventoryCommand mInventoryCommand;

	// The command to use as a responder to capture incoming barcode responses
	private BarcodeCommand mBarcodeResponder;
	
	// The inventory command configuration
	public InventoryCommand getCommand() { return mInventoryCommand; }

	public MainModel()
	{
		// This is the command that will be used to perform configuration changes and inventories
		mInventoryCommand = new InventoryCommand();

		// Configure the type of inventory
		mInventoryCommand.setIncludeTransponderRssi(TriState.YES);
		mInventoryCommand.setIncludeChecksum(TriState.YES);
		mInventoryCommand.setIncludePC(TriState.YES);
		
		// Use an InventoryCommand as a responder to capture all incoming inventory responses
		mInventoryResponder = new InventoryCommand();

		// Also capture the responses that were not from App commands 
		mInventoryResponder.setCaptureNonLibraryResponses(true);

		// Notify when each transponder is seen
		mInventoryResponder.setTransponderReceivedDelegate(new ITransponderReceivedDelegate() {

			int mTagsSeen = 0;
			@Override
			public void transponderReceived(TransponderData transponder, boolean moreAvailable) {
				mAnyTagSeen = true;

				//String tidMessage = transponder.getTidData() == null ? "" : HexEncoding.bytesToString(transponder.getTidData());
				//String infoMsg = String.format(Locale.US, "\nRSSI: %d  PC: %04X  CRC: %04X", transponder.getRssi(), transponder.getPc(), transponder.getCrc());
				//sendMessageNotification("EPC: " + transponder.getEpc() + infoMsg + "\nTID: " + tidMessage );
				sendMessageNotification("EPC:" + transponder.getEpc());
				mTagsSeen++;
				if (!moreAvailable) {
					sendMessageNotification("");
					Log.d("TagCount",String.format("Tags seen: %s", mTagsSeen));
				}
			}
		});

		mInventoryResponder.setResponseLifecycleDelegate( new ICommandResponseLifecycleDelegate() {
			
			@Override
			public void responseEnded() {
				if( !mAnyTagSeen) {
					sendMessageNotification("No transponders seen");
				}
			}
			
			@Override
			public void responseBegan() {
				mAnyTagSeen = false;
			}
		});

		// This command is used to capture barcode responses
		mBarcodeResponder = new BarcodeCommand();
		mBarcodeResponder.setCaptureNonLibraryResponses(true);
		mBarcodeResponder.setUseEscapeCharacter(TriState.YES);
		mBarcodeResponder.setBarcodeReceivedDelegate(new IBarcodeReceivedDelegate() {
			@Override
			public void barcodeReceived(String barcode) {
				sendMessageNotification("BC: " + barcode);
			}
		});

	
	}

	//
	// Reset the reader configuration for the switch actions, inventory and barcode commands
	//
	public void resetDevice()
	{
		if(getCommander().isConnected()) {
			getCommander().executeCommand(new FactoryDefaultsCommand());
		}
	}
	
	//
	// Update the reader configuration from the command
	// Call this after each change to the model's command
	//
	public void updateConfiguration()
	{
		if(getCommander().isConnected()) {
			mInventoryCommand.setTakeNoAction(TriState.YES);
			getCommander().executeCommand(mInventoryCommand);
		}
	}
	
	//
	// Perform an inventory scan with the current command parameters
	//
	public void scan()
	{
		testForAntenna();
		if(getCommander().isConnected()) {
			mInventoryCommand.setTakeNoAction(TriState.NO);
			getCommander().executeCommand(mInventoryCommand);
		}
	}


	//
	// Test for the presence of the antenna
	//
	public void testForAntenna()
	{
		if(getCommander().isConnected()) {
			InventoryCommand testCommand = InventoryCommand.synchronousCommand();
			testCommand.setTakeNoAction(TriState.YES);
			getCommander().executeCommand(testCommand);
			if( !testCommand.isSuccessful() ) {
				sendMessageNotification("ER:Error! Code: " + testCommand.getErrorCode() + " " + testCommand.getMessages().toString());
			}
		}
	}
}
