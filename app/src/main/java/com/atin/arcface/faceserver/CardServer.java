package com.atin.arcface.faceserver;

import android.content.Context;
import android.util.Log;

import com.atin.arcface.activity.Application;
import com.atin.arcface.model.CardDB;
import com.atin.arcface.model.FaceRegisterInfo;
import com.atin.arcface.model.PersonDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp vận hành thư viện khuôn mặt, bao gồm đăng ký và tìm kiếm
 */
public class CardServer {
    private static final String TAG = "CardServer";
    private static CardServer carderver = null;
    private static List<CardDB> cardList;
    private Database database;

    public static CardServer getInstance() {
        if (carderver == null) {
            synchronized (CardServer.class) {
                if (carderver == null) {
                    carderver = new CardServer();
                }
            }
        }
        return carderver;
    }

    /**
     * Khởi tạo
     *
     * @param context Đối tượng bối cảnh
     * @return Việc khởi tạo có thành công hay không
     */
    public boolean init(Context context) {
        synchronized (this) {
            initDatabase();
            initCardList();
            return false;
        }
    }

    private void initDatabase() {
        database = Application.getInstance().getDatabase();
    }

    /**
     * Khởi tạo danh sách tất cả thẻ từ
     *
     */
    private void initCardList() {
        synchronized (this) {
            cardList = database.getAllActiveCards();
        }
    }

    public CardDB findCardAccess(String cardNo){
        CardDB cardDB = cardList.stream()
                .filter(item -> item.getCardNo().trim().toUpperCase().equals(cardNo.trim().toUpperCase()))
                .findAny()
                .orElse(null);
        return cardDB;
    }
}
