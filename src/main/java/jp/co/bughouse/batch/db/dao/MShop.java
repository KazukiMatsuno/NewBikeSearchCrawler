package jp.co.bughouse.batch.db.dao;

import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQIndex;
import com.iciql.Iciql.IQIndexes;
import com.iciql.Iciql.IQSchema;
import com.iciql.Iciql.IQTable;
import com.iciql.Iciql.IndexType;
import java.io.Serializable;

@IQTable(name="m_shop")
@IQIndex(name="telNonDelimited", type=IndexType.UNIQUE, value={ "telNonDelimited" })
public class MShop implements Serializable {

	private static final long serialVersionUID = 1L;

	@IQColumn(primaryKey=true, autoIncrement=true, nullable=false)
	public Integer id;

	@IQColumn(length=255, nullable=false)
	public String address;

	@IQColumn
	public Double lat;

	@IQColumn
	public Double lng;

	@IQColumn(length=255, nullable=false)
	public String name;

	@IQColumn(length=13)
	public String tel;

	@IQColumn(length=13)
	public String telnondelimited;

	public MShop() {
	}
	
	@Override
	public String toString(){
		return id + ":" + address + ":" + lat + ":" + lng + ":" + name + ":" + tel + ":" + telnondelimited;
	}
}