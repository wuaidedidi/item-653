import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCurrentUser, updateCurrentUser, changePassword } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')
  
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 1)
  const username = computed(() => user.value?.username || '')
  const nickname = computed(() => user.value?.nickname || user.value?.username || '')
  
  // 设置用户信息
  function setUser(userInfo) {
    user.value = userInfo
    if (userInfo) {
      localStorage.setItem('user', JSON.stringify(userInfo))
    }
  }
  
  // 设置token
  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  // 登录
  function login(tokenValue, userInfo) {
    setToken(tokenValue)
    setUser(userInfo)
  }
  
  // 登出
  function logout() {
    user.value = null
    token.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
  
  // 获取用户信息
  async function fetchUser() {
    try {
      const res = await getCurrentUser()
      setUser(res.data)
      return res.data
    } catch (error) {
      logout()
      throw error
    }
  }
  
  // 更新用户信息
  async function updateUser(data) {
    const res = await updateCurrentUser(data)
    setUser(res.data)
    return res.data
  }
  
  // 修改密码
  async function updatePassword(data) {
    await changePassword(data)
  }
  
  // 初始化
  function init() {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      try {
        user.value = JSON.parse(storedUser)
      } catch (e) {
        localStorage.removeItem('user')
      }
    }
  }
  
  // 初始化调用
  init()
  
  return {
    user,
    token,
    isLoggedIn,
    isAdmin,
    username,
    nickname,
    setUser,
    setToken,
    login,
    logout,
    fetchUser,
    updateUser,
    updatePassword
  }
})
