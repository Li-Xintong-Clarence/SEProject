// mock 数据 - 预订记录（用户个人中心用）
let mockBookings = [
    { id: 1, scooterId: 1, scooterName: '滑板车 A', startTime: '2025-03-10 10:00', endTime: '2025-03-10 11:00', totalPrice: 5, status: 'active' },
    { id: 2, scooterId: 3, scooterName: '滑板车 C', startTime: '2025-03-09 14:00', endTime: '2025-03-09 15:00', totalPrice: 5, status: 'completed' },
]

// 创建预订
export const createBooking = (data) => {
    return new Promise((resolve) => {
        setTimeout(() => {
            const newBooking = {
                id: Date.now(),
                ...data,
                status: 'active',
                totalPrice: data.hours * 5, // 简单计算
            }
            mockBookings.push(newBooking)
            resolve({
                data: newBooking
            })
        }, 500)
    })
}

// 获取用户预订列表
export const getUserBookings = () => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve({
                data: mockBookings
            })
        }, 500)
    })
}

// 取消预订
export const cancelBooking = (id) => {
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            const index = mockBookings.findIndex(b => b.id === id)
            if (index !== -1) {
                mockBookings[index].status = 'cancelled'
                resolve({ data: { success: true } })
            } else {
                reject({ message: '预订不存在' })
            }
        }, 500)
    })
}