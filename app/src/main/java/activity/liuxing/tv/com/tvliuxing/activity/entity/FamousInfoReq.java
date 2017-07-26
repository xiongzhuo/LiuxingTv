package activity.liuxing.tv.com.tvliuxing.activity.entity;

import java.io.Serializable;

/**
 * Created by Xiho on 2016/3/14.
 */
public class FamousInfoReq implements Serializable {
    public String accountId;
    public String page; //关键字

    public FamousInfoReq(String accountId, String page) {
        this.accountId = accountId;
        this.page = page;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getPage() {
        return page;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setPage(String page) {
        this.page = page;
    }
}
