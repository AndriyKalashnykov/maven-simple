package http.client.model;

import com.fasterxml.jackson.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "page",
        "per_page",
        "total",
        "total_pages",
        "data"
})

public class Page {

    protected static final Logger LOGGER = LoggerFactory.getLogger(Page.class);

    @JsonProperty("page")
    public Integer page;
    @JsonProperty("per_page")
    public Integer perPage;
    @JsonProperty("total")
    public Integer total;
    @JsonProperty("total_pages")
    public Integer totalPages;
    @JsonProperty("data")
    public List<User> data = null;
    @JsonIgnore
    public Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("page")
    public Integer getPage() {
        return page;
    }

    @JsonProperty("page")
    public void setPage(Integer page) {
        this.page = page;
    }

    @JsonProperty("per_page")
    public Integer getPerPage() {
        return perPage;
    }

    @JsonProperty("per_page")
    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    @JsonProperty("total")
    public Integer getTotal() {
        return total;
    }

    @JsonProperty("total")
    public void setTotal(Integer total) {
        this.total = total;
    }

    @JsonProperty("total_pages")
    public Integer getTotalPages() {
        return totalPages;
    }

    @JsonProperty("total_pages")
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    @JsonProperty("data")
    public List<User> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<User> data) {
        this.data = data;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public void printUsers() {

        boolean printAll = false;
        List<User> users = this.getData();
        Iterator<User> i = users.iterator();

        int threshold = 10;

        while (i.hasNext()) {
            User user  = (User) i.next();
            if (user.getCommentCount().compareTo(Integer.valueOf(String.valueOf(threshold))) > 0) {
                LOGGER.info(user.getUsername().toString() + " : " + user.getCommentCount().toString());
            }
        }

        if (printAll) {
            for (User user : users) {
                LOGGER.info(user.getSubmissionCount().toString());
            }
        }
    }
}