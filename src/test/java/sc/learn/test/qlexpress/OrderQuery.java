package sc.learn.test.qlexpress;

import java.util.Date;

public class OrderQuery {

	private Date createDate;
	private String buyer;
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getBuyer() {
		return buyer;
	}
	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}
}
