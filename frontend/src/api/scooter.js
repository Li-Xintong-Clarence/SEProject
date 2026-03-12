// mock 数据 - 滑板车列表
const mockScooters = [
    { id: 1, name: '滑板车 A', status: 'available', pricePerHour: 5, location: 'A区', lat: 39.909, lng: 116.397 },
    { id: 2, name: '滑板车 B', status: 'unavailable', pricePerHour: 5, location: 'B区', lat: 39.909, lng: 116.397 },
    { id: 3, name: '滑板车 C', status: 'available', pricePerHour: 5, location: 'C区', lat: 39.909, lng: 116.397 },
    { id: 4, name: '滑板车 D', status: 'available', pricePerHour: 5, location: 'D区', lat: 39.909, lng: 116.397 },
    { id: 5, name: '滑板车 E', status: 'available', pricePerHour: 5, location: 'E区', lat: 39.909, lng: 116.397 },
]

// 获取所有滑板车
export const getScooters = () => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve({
                data: mockScooters
            })
        }, 500) // 模拟网络延迟
    })
}

// 获取可用滑板车
export const getAvailableScooters = () => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve({
                data: mockScooters.filter(s => s.status === 'available')
            })
        }, 500)
    })
}