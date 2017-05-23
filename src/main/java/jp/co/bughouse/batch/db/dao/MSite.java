package jp.co.bughouse.batch.db.dao;

import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQIndex;
import com.iciql.Iciql.IQIndexes;
import com.iciql.Iciql.IQSchema;
import com.iciql.Iciql.IQTable;
import com.iciql.Iciql.IndexType;
import java.io.Serializable;

@IQTable(name = "m_site")
@IQIndex(name = "url", type = IndexType.UNIQUE, value = {"url"})
public class MSite implements Serializable {

    private static final long serialVersionUID = 1L;

    @IQColumn(primaryKey = true, autoIncrement = true, nullable = false)
    public Integer id;

    @IQColumn(length = 32, nullable = false)
    public String name;

    @IQColumn(length = 255, nullable = false)
    public String url;

    public MSite() {
    }
}
