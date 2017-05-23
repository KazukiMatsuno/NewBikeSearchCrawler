package jp.co.bughouse.batch.db.dao;

import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQIndex;
import com.iciql.Iciql.IQIndexes;
import com.iciql.Iciql.IQSchema;
import com.iciql.Iciql.IQTable;
import com.iciql.Iciql.IndexType;
import java.io.Serializable;
import java.sql.Date;

@IQTable(name = "t_ads")
@IQIndexes({
    @IQIndex(name = "FK_shopId", type = IndexType.UNIQUE, value = {"FK_shopId", "url"}),
    @IQIndex(name = "idx_url", value = {"url"}),
    @IQIndex(name = "FK_shopId_Index", value = {"FK_shopId"})})
public class TAds implements Serializable {

    private static final long serialVersionUID = 1L;

    @IQColumn(primaryKey = true, autoIncrement = true, nullable = false)
    public Integer id;

    @IQColumn(nullable = false)
    public Integer fk_shopid;

    @IQColumn(length = 64)
    public String color;

    @IQColumn(length = 255)
    public String comment;

    @IQColumn
    public Integer distance;

    @IQColumn(defaultValue = "1")
    public Boolean flag;

    @IQColumn
    public Date insertdate;

    @IQColumn
    public Integer inspection;

    @IQColumn(length = 255, nullable = false)
    public String maker;

    @IQColumn(length = 255, nullable = false)
    public String name;

    @IQColumn(length = 255)
    public String picurl;

    @IQColumn
    public Integer price;

    @IQColumn
    public Integer rank;

    @IQColumn(nullable = false)
    public Date updatedate;

    @IQColumn(length = 255, nullable = false)
    public String url;

    @IQColumn
    public Integer year;

    public TAds() {
    }
}
