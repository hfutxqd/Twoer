package xyz.imxqd.ta.utils;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by imxqd on 17-4-2.
 */

public class Serializer {
    public static byte[] objectToByte(Serializable obj) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);

            bytes = bo.toByteArray();

            bo.close();
            oo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static Serializable byteToObject(byte[] bytes) {
        Serializable obj = null;
        try {
            // bytearray to object
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);
            obj = (Serializable) oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return obj;
    }

    public static String objectToBase64(Serializable obj) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);

            bytes = bo.toByteArray();

            bo.close();
            oo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static Serializable base64ToObject(String base64) {
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        return byteToObject(data);
    }
}
