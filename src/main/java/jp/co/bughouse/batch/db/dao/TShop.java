package jp.co.bughouse.batch.db.dao;

import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQIndex;
import com.iciql.Iciql.IQIndexes;
import com.iciql.Iciql.IQSchema;
import com.iciql.Iciql.IQTable;
import com.iciql.Iciql.IndexType;
import java.io.Serializable;

@IQTable(name = "t_shop")
@IQIndexes({
    @IQIndex(name = "FK_shopId", value = {"FK_shopId"}),
    @IQIndex(name = "FK_siteId", type = IndexType.UNIQUE, value = {"FK_siteId", "url"}),
    @IQIndex(name = "rank", value = {"rank"})})
public class TShop implements Serializable {

    private static final long serialVersionUID = 1L;

    @IQColumn(primaryKey = true, autoIncrement = true, nullable = false)
    public Integer id;

    @IQColumn(nullable = false)
    public Integer fk_shopid;

    @IQColumn(nullable = false)
    public Integer fk_siteid;

    @IQColumn(defaultValue = "1")
    public Boolean flag;

    @IQColumn(defaultValue = "0")
    public Double rank;

    @IQColumn(defaultValue = "0")
    public Integer total;

    @IQColumn(length = 255, nullable = false)
    public String url;

    public TShop() {
    }
}
