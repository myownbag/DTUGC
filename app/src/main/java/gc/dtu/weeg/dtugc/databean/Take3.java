package gc.dtu.weeg.dtugc.databean;


//@lombok.NoArgsConstructor
//@lombok.Data
public class Take3 {


//    @com.fasterxml.jackson.annotation.JsonProperty("data")
    public DataDTO data;

    public String errorCode;

    public String message;

    public String moduleCode;

    public Boolean result;

    public String token;

    public Integer total;

    public static class DataDTO {

        public DataanalysisResult  analysisResult;
    }

    public static class DataanalysisResult {
       public String csq;
       public String curprice;
       public String gleft;
       public String gsum;
       public  String switchstate;
    }
}
