package com.example.demo.service;

/**
 * �ʼ�����ӿ�
 * ���巢���ʼ�����ز���
 */
public interface EmailService {
    /**
     * ����Ԥ��ȷ����??
     * @param toEmail �ռ�����??
     * @param username �û�??
     * @param confirmationCode Ԥ��ȷ��??
     * @param scooterNumber �綯����??
     * @param hireOption ����ѡ��
     * @param startTime ��ʼʱ??
     * @param endTime ����ʱ��
     * @param totalCost �ܷ�??
     */
    void sendBookingConfirmation(String toEmail, String username, String confirmationCode,
                                 String scooterNumber, String hireOption, String startTime, String endTime, double totalCost);

    /**
     * ���Ͷ���ȡ����??
     */
    void sendBookingCancellation(String toEmail, String username, String confirmationCode,
                                 String scooterNumber, String hireOption);

    /**
     * ���ͽ���������??
     */
    void sendRideCompletion(String toEmail, String username, String confirmationCode,
                           String scooterNumber, String startTime, String endTime, double totalCost);

    /**
     * �����Զ�����֪ͨ�ʼ�
     * @param toEmail �ռ�����??
     * @param username �û�??
     * @param confirmationCode Ԥ��ȷ��??
     * @param scooterNumber �綯����??
     * @param startTime ��ʼʱ??
     * @param endTime ����ʱ��
     * @param depotName �����������??
     * @param totalCost �ܷ�??
     */
    void sendAutoReturnNotification(String toEmail, String username, String confirmationCode,
                                    String scooterNumber, String startTime, String endTime,
                                    String depotName, double totalCost);
}
