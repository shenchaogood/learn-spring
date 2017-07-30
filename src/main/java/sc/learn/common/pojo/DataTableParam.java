package sc.learn.common.pojo;

import java.util.ArrayList;
import java.util.List;


/**
 * start 开始角标 0开始
 * length 查找数量
 * columns List<Column>集合
 * 					data  列的属性名  对于where查找写对象的属性名如userId, 对于order by排序需要些真实列名如f_user_id
 * 					searchable 此属性是否可用于 search对象模糊查找，见search属性
 * 					orderable 此属性是否可用于排序 此时data是f_user_id
 *					search 检索对象
 *						value 检索的具体值
 *						regex 按正则表达式查找(暂不支持)
 * order List<Order>集合
 * 					column  需要按照某列排序的角标0开始
 * 					dir     asc正序 desc倒序
 * 
 * search 			全局模糊查找,会按照columns中searchable等于true的列模糊查找  where column.data like 'search.value%'
 *
 */
public class DataTableParam {
	
	public static class Search{
		private Object value;
		private boolean regex;
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
		public boolean isRegex() {
			return regex;
		}
		public void setRegex(boolean regex) {
			this.regex = regex;
		}
		@Override
		public String toString() {
			return "Search [value=" + value + ", regex=" + regex + "]";
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
		@Override
		public String toString() {
			return "Order [column=" + column + ", dir=" + dir + "]";
		}
	}
	
	public static class Column{
		private String data;
		private String name;
		private boolean searchable;
		private boolean orderable;
		private Search search=new Search();
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
		@Override
		public String toString() {
			return "Column [data=" + data + ", name=" + name + ", searchable=" + searchable + ", orderable=" + orderable + ", search=" + search + "]";
		}
	}
	
	private int draw;
	
	private int start;
	
	private int length;

	private List<Column> columns=new ArrayList<>(0);
	
	private List<Order> order=new ArrayList<>(0);
	
	private Search search=new Search();

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Order> getOrder() {
		return order;
	}

	public void setOrder(List<Order> order) {
		this.order = order;
	}

	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}

	@Override
	public String toString() {
		return "DataTableParam [draw=" + draw + ", start=" + start + ", length=" + length + ", columns=" + columns + ", order=" + order + ", search=" + search + "]";
	}
	
}
