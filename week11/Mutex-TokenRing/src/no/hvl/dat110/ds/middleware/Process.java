package no.hvl.dat110.ds.middleware;


/**
 * @author tdoy
 * For demo/teaching purpose at dat110 class
 * Mutual Exclusion using Token Ring Algorithm.
 * Basic implementation with no fault-tolerance
 */

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import no.hvl.dat110.ds.middleware.iface.OperationType;
import no.hvl.dat110.ds.middleware.iface.ProcessInterface;
import no.hvl.dat110.ds.util.Util;

public class Process extends UnicastRemoteObject implements ProcessInterface {
	

	private static final long serialVersionUID = 1L;

	private List<Message> queue;					// queue for this process
	private int processID;							// id of this process
	private double balance = 1000;					// default balance (each replica has the same). Our goal is to avoid concurrent access 
	private Token token = null;						// token to be passed in the ring
	private ProcessInterface successor;				// each process has the knowledge of its successor
	private ExecutorService backgroundExec = Executors.newCachedThreadPool();
	
	protected Process(int id) throws RemoteException {
		super();
		processID = id;
		queue = new ArrayList<Message>();	
	}
	
	private void updateDeposit(double amount) throws RemoteException {

		balance += amount;
	}
	
	private void updateInterest(double interest) throws RemoteException {

		double intvalue = balance*interest;
		balance += intvalue;
	}
	
	private void updateWithdrawal(double amount) throws RemoteException {

		balance -= amount;
	}
	
	// client initiated method
	@Override
	public void requestInterest(double interest) throws RemoteException {
		// 		
		Message message = new Message();
		message.setOptype(OperationType.INTEREST);		// set the type of message - interest
		message.setProcessID(processID);				// set the process ID
		message.setInterest(interest); 					// add interest to calculate as part of message

		// add message to queue
		queue.add(message);
		// TODO 		
		// make a new message instance and set the following:
		// set the type of message - interest
		// set the process ID
		// set the interest
		
		// add message to queue

	}
	
	// client initiated method
	@Override
	public void requestDeposit(double amount) throws RemoteException {
		// 		
		Message message = new Message();
		message.setOptype(OperationType.DEPOSIT);		// set the type of message - deposit
		message.setProcessID(processID); 				// set the process ID
		message.setDepositamount(amount); 				// add amount to deposit as part of message
		
		// add message to queue
		queue.add(message);
		// TODO 		
		// make a new message instance and set the following
		// set the type of message - deposit
		// set the process ID
		// set the deposit amount
		
		// add message to queue

	}
	
	// client initiated method
	@Override
	public void requestWithdrawal(double amount) throws RemoteException {
		// 		
		Message message = new Message();
		message.setOptype(OperationType.WITHDRAWAL);	// set the type of message - withdrawal
		message.setProcessID(processID); 				// set the process ID
		message.setWithdrawamount(amount);				// add amount to withdraw as part of message

		// add message to queue
		queue.add(message);
		// TODO 		
		// make a new message instance and set the following
		// set the type of message - withdrawal
		// set the process ID
		// set the withdrawal amount
		
		// add message to queue

	}	

	@Override
	public void forwardToken() throws RemoteException {
		
		Token token = new Token(this.token.getTokenId());
		this.token = null;
		successor.onTokenReceived(token);
		
	}

	/**
	 * @param successor the successor to set
	 */
	public void setSuccessor(ProcessInterface successor) throws RemoteException {
		this.successor = successor;
	}
	
	public void applyOperation() throws RemoteException {

//		if(token == null)
//			return;

		for(int i=0; i<queue.size(); i++) {

			Message message = queue.get(i);
			
			OperationType optype = message.getOptype();

			switch(optype) {
			
				case DEPOSIT: 
					updateDeposit(message.getDepositamount());
					break;
					
				case INTEREST: 
					updateInterest(message.getInterest());
					break;
					
				case WITHDRAWAL: 
					updateWithdrawal(message.getWithdrawamount());
					break;
					
				default: break;
			}

		}
		// TODO
		
		// iterate over the queue
		
		// for each message in the queue, check the operation type
		
		// call the appropriate update method for the operation type and pass the value to be updated

		Util.printQueue(this);
		// clear the queue
		queue.clear();
//		// forward the token to the successor process by calling the forwardToken method
//		this.forwardToken();		
	}
	
	@Override
	public double getBalance() throws RemoteException {
		return balance;
	}
	
	@Override
	public int getProcessID() throws RemoteException {
		return processID;
	}
	
	@Override
	public List<Message> getQueue() throws RemoteException {
		return queue;
	}

	@Override
	public void onTokenReceived(Token token) throws RemoteException {
		// TODO
		// check whether token is null
		// if no, set the token object to the received token
		// call applyOperation method
		// forward the token to the successor process by calling the forwardToken method
		if(token == null)
			return;
		System.out.println(this.processID+" has token: tokenId = "+token.getTokenId());
		this.token = token;
		applyOperation();		
		this.forwardToken();

	}

	/**
	 * Give this job to a different thread
	 */
	@Override
	public void requestToken(ProcessInterface requester) throws RemoteException {
		
		backgroundExec.execute(new Runnable() {

			@Override
			public void run() {

				ProcessInterface tokmanager = Util.getProcessStub(TokenManager.TOKENMANAGER, Config.PORT4);
				try {
					tokmanager.requestToken(requester);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
			}
			
		});
	
	}
	
}
