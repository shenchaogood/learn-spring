package sc.learn.common.pojo;

public class DataTableParam {
	
	public static class Search{
		private String value;
		private boolean regex;
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public boolean isRegex() {
			return regex;
		}
		public void setRegex(boolean regex) {
			this.regex = regex;
		}
	}
	
	public static class Order{
		private int column;
		private String dir;
		public int getColumn() {
			return column;
		}
		public void setColumn(int column) {
			this.column = column;
		}
		public String getDir() {
			return dir;
		}
		public void setDir(String dir) {
			this.dir = dir;
		}
	}
	
	public static class Column{
		private String data;
		private String name;
		private boolean searchable;
		private boolean orderable;
		private Search search;
		private Order order;
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public boolean isSearchable() {
			return searchable;
		}
		public void setSearchable(boolean searchable) {
			this.searchable = searchable;
		}
		public boolean isOrderable() {
			return orderable;
		}
		public void setOrderable(boolean orderable) {
			this.orderable = orderable;
		}
		public Search getSearch() {
			return search;
		}
		public void setSearch(Search search) {
			this.search = search;
		}
		public Order getOrder() {
			return order;
		}
		public void setOrder(Order order) {
			this.order = order;
		}
		
	}
	
	/////
	private long draw;
	
	private long start;
	
	private long length;
	
	private Column[] columns;
	
	private Order order;
	
	private Search search;

	public long getDraw() {
		return draw;
	}

	public void setDraw(long draw) {
		this.draw = draw;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public Column[] getColumns() {
		return columns;
	}

	public void setColumns(Column[] columns) {
		this.columns = columns;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}
}
