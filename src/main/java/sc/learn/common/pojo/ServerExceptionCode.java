package sc.learn.common.pojo;

public enum ServerExceptionCode {
	
	DB_INSERT_ERROR,
	DB_UPDATE_ERROR,
	DB_DELETE_ERROR,
	;
	
	@Override
	public String toString() {
		return super.toString().toLowerCase().replace("_", " ");
	}

}
