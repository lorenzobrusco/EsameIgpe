package multiPlayer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueue<T> {

	protected Lock lock = new ReentrantLock();
	protected final Condition full_condition = lock.newCondition();
	protected final Condition empty_condition = lock.newCondition();
	protected T thebuffer[];
	protected int in, out, slotPieni;

	@SuppressWarnings("unchecked")
	public BlockingQueue(int dim) {

		in = 0;
		out = 0;
		slotPieni = 0;
		thebuffer = (T[]) new Object[dim];
	}

	public T take() throws InterruptedException {

		T returnValue;
		lock.lock();
		try{
			while (slotPieni == 0)
				try{
					empty_condition.await();
				}
				catch(InterruptedException e){}
				returnValue = thebuffer[out];
				out = (out+1) % thebuffer.length;
				if (slotPieni == thebuffer.length) 
					full_condition.signal();
				slotPieni--;
			    return returnValue;	
		}
		finally
		{
			lock.unlock();
		}
		

	}

	public void put(T c) throws InterruptedException {

		lock.lock();
		try{
			while (slotPieni == thebuffer.length)
				try {
					full_condition.await();
				}
			    catch(InterruptedException e)
			    { }
			
				thebuffer[in] = c;
				in = (in+1) % thebuffer.length;
				if (slotPieni == 0) 
					empty_condition.signal();
				slotPieni++;
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public boolean isEmpty(){
		
		lock.lock();
			try{
				return slotPieni==0;
			}
			finally{
		lock.unlock();
			}
	}
	
	
	
}