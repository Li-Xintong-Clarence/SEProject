package com.example.demo.service;

import com.example.demo.entity.Booking;
import java.util.List;
import java.util.Map;

/**
 * ��������ӿ�
 * ���嶩�������ޣ���ص�ҵ���??
 */
public interface BookingService {
    /**
     * ��ѯ���ж���������Ա��
     */
    List<Booking> findAll();
    /**
     * ��ѯ�û��Ķ�����??
     */
    List<Booking> findByUserId(Long userId);
    /**
     * ����ID��ѯ����
     */
    Booking findById(Long id);
    /**
     * �����¶�����ͨ������㣬�Զ����䳵��??
     * @param userId �û�ID
     * @param depotId �����ID�����
     * @param hireOption ����ʱ��
     */
    Booking createByDepot(Long userId, Long depotId, String hireOption);
    /**
     * �����¶�����ָ������??
     */
    boolean save(Booking booking);
    /**
     * ���¶�����Ϣ
     */
    boolean update(Booking booking);
    /**
     * ɾ������
     */
    boolean deleteById(Long id);
    /**
     * �ӳ�����
     * @param id ����ID
     * @param hireOption �ӳ���ʱ��ѡ��??hr, 4hr, 1day, 1week??
     */
    boolean extendBooking(Long id, String hireOption);
    /**
     * ȡ������
     */
    boolean cancelBooking(Long id);
    /**
     * ֧������
     */
    boolean payBooking(Long id);
    /**
     * ��ȡ�û�ͳ����Ϣ���������������ѡ���ʱ����
     */
    Map<String, Object> getUserStats(Long userId);
    /**
     * �������������У�
     * @param id ����ID
     */
    boolean returnScooter(Long id);
}
