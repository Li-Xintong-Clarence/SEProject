import request from '@/utils/request'

// 验证卡号格式
export const validateCard = (cardNumber) => {
    return request({
        url: '/api/payments/validate/card',
        method: 'post',
        data: { cardNumber }
    })
}

// 验证CVV
export const validateCVV = (cvv, cardType) => {
    return request({
        url: '/api/payments/validate/cvv',
        method: 'post',
        data: { cvv, cardType }
    })
}

// 验证有效期
export const validateExpiry = (expiry) => {
    return request({
        url: '/api/payments/validate/expiry',
        method: 'post',
        data: { expiry }
    })
}

// 绑定银行卡
export const bindCard = (cardNumber, expiry, cvv) => {
    return request({
        url: '/api/payments/bind',
        method: 'post',
        data: { cardNumber, expiry, cvv }
    })
}

// 解除银行卡绑定
export const unbindCard = () => {
    return request({
        url: '/api/payments/unbind',
        method: 'post'
    })
}

// 获取绑定的银行卡信息
export const getCardInfo = () => {
    return request({
        url: '/api/payments/card',
        method: 'get'
    })
}

// 设置支付密码
export const setPaymentPassword = (password) => {
    return request({
        url: '/api/payments/password/set',
        method: 'post',
        data: { password }
    })
}

// 验证支付密码
export const verifyPaymentPassword = (password) => {
    return request({
        url: '/api/payments/password/verify',
        method: 'post',
        data: { password }
    })
}

// Token支付
export const payWithToken = (amount, paymentPassword) => {
    return request({
        url: '/api/payments/pay',
        method: 'post',
        data: { amount, paymentPassword }
    })
}

// 生成卡号Token
export const generateCardToken = (cardNumber) => {
    return request({
        url: '/api/payments/token',
        method: 'post',
        data: { cardNumber }
    })
}

// 掩码卡号
export const maskCard = (cardNumber) => {
    return request({
        url: '/api/payments/mask',
        method: 'post',
        data: { cardNumber }
    })
}

// 卡类型检测
export const detectCardType = (cardNumber) => {
    if (!cardNumber) return 'UNKNOWN'
    const cleaned = cardNumber.replace(/[\s-]/g, '')

    // VISA
    if (/^4/.test(cleaned) && (cleaned.length === 13 || cleaned.length === 16)) {
        return 'VISA'
    }
    // MasterCard
    if (/^5[1-5]/.test(cleaned) && cleaned.length === 16) {
        return 'MASTERCARD'
    }
    if (/^2[2-7]/.test(cleaned) && cleaned.length === 16) {
        return 'MASTERCARD'
    }
    // American Express
    if (/^3[47]/.test(cleaned) && cleaned.length === 15) {
        return 'AMEX'
    }
    // Discover
    if (/^6(?:011|5)/.test(cleaned) && cleaned.length === 16) {
        return 'DISCOVER'
    }

    return 'UNKNOWN'
}

// Luhn算法验证
export const validateCardNumber = (cardNumber) => {
    if (!cardNumber) return false
    const cleaned = cardNumber.replace(/[\s-]/g, '')

    if (!/^\d{13,19}$/.test(cleaned)) return false

    let sum = 0
    let isEven = false

    for (let i = cleaned.length - 1; i >= 0; i--) {
        let digit = parseInt(cleaned.charAt(i), 10)

        if (isEven) {
            digit *= 2
            if (digit > 9) digit -= 9
        }

        sum += digit
        isEven = !isEven
    }

    return sum % 10 === 0
}

// 格式化卡号（每4位一组）
export const formatCardNumber = (value) => {
    if (!value) return ''
    const cleaned = value.replace(/[\s-]/g, '')
    return cleaned.replace(/(\d{4})(?=\d)/g, '$1 ')
}

// 格式化有效期（MM/YY）
export const formatExpiry = (value) => {
    if (!value) return ''
    const cleaned = value.replace(/\D/g, '')
    if (cleaned.length >= 2) {
        return cleaned.substring(0, 2) + '/' + cleaned.substring(2, 4)
    }
    return cleaned
}
