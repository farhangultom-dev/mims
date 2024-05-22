package dev.iconpln.mims.data.local.database;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "TTRANS_DATA_RATING".
 */
@Entity
public class TTransDataRating {

    @Id
    private Long id;
    private Integer Ketepatan;
    private String RatingQuality;
    private String RatingDelivery;
    private Boolean SelesaiRating;
    private String NoDoSmar;
    private String RatingResponse;
    private String NoRating;
    private Integer IsDone;

    @Generated
    public TTransDataRating() {
    }

    public TTransDataRating(Long id) {
        this.id = id;
    }

    @Generated
    public TTransDataRating(Long id, Integer Ketepatan, String RatingQuality, String RatingDelivery, Boolean SelesaiRating, String NoDoSmar, String RatingResponse, String NoRating, Integer IsDone) {
        this.id = id;
        this.Ketepatan = Ketepatan;
        this.RatingQuality = RatingQuality;
        this.RatingDelivery = RatingDelivery;
        this.SelesaiRating = SelesaiRating;
        this.NoDoSmar = NoDoSmar;
        this.RatingResponse = RatingResponse;
        this.NoRating = NoRating;
        this.IsDone = IsDone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getKetepatan() {
        return Ketepatan;
    }

    public void setKetepatan(Integer Ketepatan) {
        this.Ketepatan = Ketepatan;
    }

    public String getRatingQuality() {
        return RatingQuality;
    }

    public void setRatingQuality(String RatingQuality) {
        this.RatingQuality = RatingQuality;
    }

    public String getRatingDelivery() {
        return RatingDelivery;
    }

    public void setRatingDelivery(String RatingDelivery) {
        this.RatingDelivery = RatingDelivery;
    }

    public Boolean getSelesaiRating() {
        return SelesaiRating;
    }

    public void setSelesaiRating(Boolean SelesaiRating) {
        this.SelesaiRating = SelesaiRating;
    }

    public String getNoDoSmar() {
        return NoDoSmar;
    }

    public void setNoDoSmar(String NoDoSmar) {
        this.NoDoSmar = NoDoSmar;
    }

    public String getRatingResponse() {
        return RatingResponse;
    }

    public void setRatingResponse(String RatingResponse) {
        this.RatingResponse = RatingResponse;
    }

    public String getNoRating() {
        return NoRating;
    }

    public void setNoRating(String NoRating) {
        this.NoRating = NoRating;
    }

    public Integer getIsDone() {
        return IsDone;
    }

    public void setIsDone(Integer IsDone) {
        this.IsDone = IsDone;
    }

}
