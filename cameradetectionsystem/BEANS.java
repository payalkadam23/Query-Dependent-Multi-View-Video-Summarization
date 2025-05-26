/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cameradetectionsyatem;

import java.util.ArrayList;

public class BEANS {
  public static  ArrayList<String> MatchFrames = new ArrayList<>();

    public static ArrayList<String> getMatchFrames() {
        return MatchFrames;
    }

    public static void setMatchFrames(ArrayList<String> MatchFrames) {
        BEANS.MatchFrames = MatchFrames;
    }

   
    
}
