package gc.dtu.weeg.dtugc.databean;


//@lombok.NoArgsConstructor
//@lombok.Data
public class Take2 {


//    @com.fasterxml.jackson.annotation.JsonProperty("data")
    public DataDTO data;

    public String errorCode;

    public String message;

    public String moduleCode;

    public Boolean result;

    public String token;

    public Integer total;

    public static class DataDTO {

            public String analysisResult;
    }
}
