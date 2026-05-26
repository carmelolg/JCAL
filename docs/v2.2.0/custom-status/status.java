public class GoLStatus extends DefaultStatus {

	public boolean isAlive;

	public GoLStatus(boolean isAlive) {
		super("GoLStatus", null);
		this.isAlive = isAlive;
	}

	@Override
	public String toString() {
		return isAlive ? "1 " : "0 ";
	}

}
