package org.d3ifcool.addictcontrol;

/**
 * Created by cool on 9/27/2018.
 */

public class Kid {
   private String nama;
   private int image;
   private String time;
   private int emoteImage;

   public Kid(String nama, int image, String time, int emoteImage) {
      this.nama = nama;
      this.image = image;
      this.time = time;
      this.emoteImage = emoteImage;
   }

   public String getNama() {
      return nama;
   }

   public void setNama(String nama) {
      this.nama = nama;
   }

   public int getImage() {
      return image;
   }

   public void setImage(int image) {
      this.image = image;
   }

   public String getTime() {
      return time;
   }

   public void setTime(String time) {
      this.time = time;
   }

   public int getEmoteImage() {
      return emoteImage;
   }

   public void setEmoteImage(int emoteImage) {
      this.emoteImage = emoteImage;
   }
}
