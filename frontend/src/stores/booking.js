// src/stores/booking.js
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useBookingStore = defineStore('booking', () => {
  const activeBooking = ref(null)
  const bookings = ref([])

  function setActiveBooking(booking) {
    activeBooking.value = booking
  }

  function setBookings(list) {
    bookings.value = list
  }

  function clearActiveBooking() {
    activeBooking.value = null
  }

  return {
    activeBooking,
    bookings,
    setActiveBooking,
    setBookings,
    clearActiveBooking
  }
})
