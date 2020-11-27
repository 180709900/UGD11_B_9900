package com.pbp.gd11_x_yyyy;

import java.io.Serializable;

public class Download implements Serializable {
    private String namaFile , url;

    public Download(String namaFile, String url) {
        this.namaFile = namaFile;
        this.url = url;
    }

    public String getNamaFile() {
        return namaFile;
    }

    public void setNamaFile(String namaFile) {
        this.namaFile = namaFile;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
