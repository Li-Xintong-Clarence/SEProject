// 短信验证码 API
import request from '@/utils/request'

/**
 * 发送验证码
 * @param {string} phone 手机号
 * @param {string} type 验证码类型：LOGIN, REGISTER, PASSWORD_RESET, BIND_PHONE
 */
export const sendSmsCode = (phone, type = 'LOGIN') => {
  return request({
    url: '/api/sms/send',
    method: 'post',
    data: { phone, type }
  })
}

/**
 * 验证验证码
 * @param {string} phone 手机号
 * @param {string} code 验证码
 * @param {string} type 验证码类型
 */
export const verifySmsCode = (phone, code, type = 'LOGIN') => {
  return request({
    url: '/api/sms/verify',
    method: 'post',
    data: { phone, code, type }
  })
}
