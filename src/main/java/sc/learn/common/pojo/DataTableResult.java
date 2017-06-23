package sc.learn.common.pojo;

import java.util.List;

public class DataTableResult<T> {

	public static <T> DataTableResult<T> createDataTableResult(long draw, long recordsTotal, long recordsFiltered, List<T> data,String error){
		return new DataTableResult<T>(draw,recordsTotal,recordsFiltered,data,error);
	}
	
	private DataTableResult(long draw, long recordsTotal, long recordsFiltered, List<T> data,String error) {
		this.draw = draw;
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
		this.data = data;
		this.error = error;
	}

	private long draw;//请求次数
	
	private long recordsTotal;
	
	private long recordsFiltered;
	
	private List<T> data;
	
	private String error;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public long getDraw() {
		return draw;
	}

	public void setDraw(long draw) {
		this.draw = draw;
	}

	public long getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(long recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public long getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
}
