package gc.dtu.weeg.dtugc.databean;


public class Take {


    public Boolean result;

    public String moduleCode;

    public String errorCode;

    public String token;

    public String message;

    public Integer total;

    public DataDTO data;


    public static class DataDTO {

        public String icType;
        public String icSerial;
        public String cmdType;
        public String icCmd;
    }
}
