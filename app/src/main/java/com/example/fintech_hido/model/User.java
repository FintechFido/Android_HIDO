package com.example.fintech_hido.model;

public class User
{
    private String session_key;
    private int running_code;
    private int saved_code;
    private String my_imei;

    private static User user = new User();

    // 싱글톤 패턴으로 구현
    private User()
    {

    }

    public static User getInstance(){
        return user;
    }

    public void set_info(String session_key, int running_code, int saved_code, String imei)
    {
        this.session_key = session_key;
        this.running_code = running_code;
        this.saved_code = saved_code;
        this.my_imei = imei;
    }

    public String get_session_key()
    {
        return session_key;
    }
    public int get_running_code()
    {
        return running_code;
    }
    public int get_saved_code()
    {
        return saved_code;
    }
    public String get_imei()
    {
        return my_imei;
    }

}
