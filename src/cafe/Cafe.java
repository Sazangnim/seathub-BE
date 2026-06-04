package cafe;

public class Cafe {

    private int    cafeId;
    private int    userId;
    private String cafeName;
    private String region;
    private String address;
    private String tags;

    // 조회용 생성자
    public Cafe(int cafeId, int userId, String cafeName,
                String region, String address, String tags) {
        this.cafeId   = cafeId;
        this.userId   = userId;
        this.cafeName = cafeName;
        this.region   = region;
        this.address  = address;
        this.tags     = tags;
    }

    // 등록용 생성자
    public Cafe(int userId, String cafeName, String region, String address) {
        this.userId   = userId;
        this.cafeName = cafeName;
        this.region   = region;
        this.address  = address;
    }

    public int    getCafeId()                  { return cafeId; }
    public void   setCafeId(int cafeId)        { this.cafeId = cafeId; }
    public int    getUserId()                  { return userId; }
    public void   setUserId(int userId)        { this.userId = userId; }
    public String getCafeName()                { return cafeName; }
    public void   setCafeName(String cafeName) { this.cafeName = cafeName; }
    public String getRegion()                  { return region; }
    public void   setRegion(String region)     { this.region = region; }
    public String getAddress()                 { return address; }
    public void   setAddress(String address)   { this.address = address; }
    public String getTags()                    { return tags; }
    public void   setTags(String tags)         { this.tags = tags; }
}