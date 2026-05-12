<template>
  <div class="admin-container">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="sidebar-logo">
          <div class="logo-icon">
            <svg viewBox="0 0 64 64" fill="none">
              <circle cx="14" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
              <circle cx="50" cy="50" r="10" stroke="currentColor" stroke-width="2.5"/>
              <path d="M14 50L24 30H40L50 50" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
              <path d="M24 30L30 20H38" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"/>
              <rect x="28" y="18" width="12" height="4" rx="1" fill="currentColor"/>
            </svg>
          </div>
          <div class="logo-text">
            <span class="brand-name">CapyGlide</span>
            <span class="brand-tag">管理后台</span>
          </div>
        </div>
      </div>

      <nav class="sidebar-nav">
        <div class="nav-section">
          <span class="nav-label">数据中心</span>
          <a class="nav-item" :class="{ active: activeTab === 'overview' }" @click="activeTab = 'overview'">
            <el-icon><DataLine /></el-icon>
            <span>运营概览</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'reports' }" @click="activeTab = 'reports'">
            <el-icon><TrendCharts /></el-icon>
            <span>收入报表</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'userAnalysis' }" @click="activeTab = 'userAnalysis'">
            <el-icon><User /></el-icon>
            <span>用户分析</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'orderAnalysis' }" @click="activeTab = 'orderAnalysis'">
            <el-icon><Tickets /></el-icon>
            <span>订单分析</span>
          </a>
        </div>

        <div class="nav-section">
          <span class="nav-label">用户与车辆</span>
          <a class="nav-item" :class="{ active: activeTab === 'users' }" @click="switchTab('users')">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'scooters' }" @click="switchTab('scooters')">
            <el-icon><Van /></el-icon>
            <span>车辆管理</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'map' }" @click="switchTab('map')">
            <el-icon><MapLocation /></el-icon>
            <span>车辆地图</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'depot' }" @click="switchTab('depot')">
            <el-icon><LocationFilled /></el-icon>
            <span>服务点管理</span>
          </a>
        </div>

        <div class="nav-section">
          <span class="nav-label">运营支持</span>
          <a class="nav-item" :class="{ active: activeTab === 'booking' }" @click="switchTab('booking')">
            <el-icon><Tickets /></el-icon>
            <span>代客预订</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'pricing' }" @click="switchTab('pricing')">
            <el-icon><Money /></el-icon>
            <span>价格配置</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'discount' }" @click="switchTab('discount')">
            <el-icon><Sell /></el-icon>
            <span>折扣管理</span>
          </a>
        </div>

        <div class="nav-section">
          <span class="nav-label">问题处理</span>
          <a class="nav-item" :class="{ active: activeTab === 'issues' }" @click="switchTab('issues')">
            <el-icon><Warning /></el-icon>
            <span>故障工单</span>
            <span v-if="pendingIssues > 0" class="badge">{{ pendingIssues }}</span>
          </a>
          <a class="nav-item" :class="{ active: activeTab === 'feedback' }" @click="switchTab('feedback')">
            <el-icon><ChatDotRound /></el-icon>
            <span>用户反馈</span>
          </a>
        </div>
      </nav>

      <div class="sidebar-footer">
        <div class="admin-info">
          <el-avatar :size="36" class="admin-avatar">{{ adminName?.charAt(0) || 'A' }}</el-avatar>
          <div class="admin-detail">
            <span class="admin-name">{{ adminName }}</span>
            <span class="admin-role">系统管理员</span>
          </div>
        </div>
        <el-button type="danger" plain size="small" @click="logout" class="logout-btn">
          <el-icon><SwitchButton /></el-icon>
        </el-button>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="main-area">
      <!-- 内容区域 -->
      <div class="content-wrapper">
        <!-- 概览 -->
        <div v-if="activeTab === 'overview'" class="tab-content">
          <!-- 核心统计 -->
          <div class="stats-grid">
            <div class="stat-card primary">
              <div class="stat-icon">
                <el-icon><Money /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">¥{{ totalIncome }}</span>
                <span class="stat-label">总收入</span>
              </div>
              <div class="stat-trend up">
                <el-icon><Top /></el-icon>
                <span>+12.5%</span>
              </div>
            </div>
            <div class="stat-card success">
              <div class="stat-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ totalUsers }}</span>
                <span class="stat-label">注册用户</span>
              </div>
              <div class="stat-sub">
                <span>今日+{{ todayNewUsers }}</span>
                <span>本周+{{ weekNewUsers }}</span>
              </div>
            </div>
            <div class="stat-card warning">
              <div class="stat-icon">
                <el-icon><Van /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ activeBookings }}</span>
                <span class="stat-label">进行中行程</span>
              </div>
              <div class="stat-sub">
                <span>使用率 {{ usageRate }}%</span>
              </div>
            </div>
            <div class="stat-card info">
              <div class="stat-icon">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="stat-info">
                <span class="stat-value">{{ pendingIssues }}</span>
                <span class="stat-label">待处理工单</span>
              </div>
            </div>
          </div>

          <!-- 运营指标 -->
          <div class="overview-row">
            <div class="chart-card">
              <div class="card-header">
                <h3>运营指标</h3>
              </div>
              <div class="metrics-grid">
                <div class="metric-item">
                  <span class="metric-label">今日收入</span>
                  <span class="metric-value">¥{{ todayIncome }}</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">本周收入</span>
                  <span class="metric-value">¥{{ weeklyIncome }}</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">可用车辆</span>
                  <span class="metric-value">{{ availableCount }}/{{ scooters.length }}</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">服务点</span>
                  <span class="metric-value">{{ depots.length }} 个</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">平均骑行时长</span>
                  <span class="metric-value">{{ avgRideDuration }} 分钟</span>
                </div>
                <div class="metric-item">
                  <span class="metric-label">订单完成率</span>
                  <span class="metric-value">{{ completionRate }}%</span>
                </div>
              </div>
            </div>
            <div class="chart-card">
              <div class="card-header">
                <h3>各租期收入分布</h3>
              </div>
              <div ref="chartByOptionRef" class="chart"></div>
            </div>
          </div>

          <!-- 每日趋势 & 车辆预警 -->
          <div class="overview-row">
            <div class="chart-card">
              <div class="card-header">
                <h3>每日收入趋势</h3>
              </div>
              <div ref="chartDailyRef" class="chart"></div>
            </div>
            <div class="chart-card">
              <div class="card-header">
                <h3>车辆健康预警</h3>
              </div>
              <div class="alert-list">
                <div v-if="lowBatteryScooters.length === 0" class="no-alert">暂无预警</div>
                <div v-for="scooter in lowBatteryScooters.slice(0, 5)" :key="scooter.id" class="alert-item danger">
                  <el-icon><Warning /></el-icon>
                  <span>{{ scooter.scooterNumber }} 电量过低 ({{ scooter.batteryLevel }}%)</span>
                </div>
                <div v-for="depot in lowStockDepots" :key="depot.id" class="alert-item warning">
                  <el-icon><Warning /></el-icon>
                  <span>{{ depot.name }} 库存不足 ({{ depot.currentStock }}/{{ depot.capacity }})</span>
                </div>
                <div v-for="scooter in maintenanceNeeded.slice(0, 3)" :key="scooter.id" class="alert-item info">
                  <el-icon><Tools /></el-icon>
                  <span>{{ scooter.scooterNumber }} 需要维护</span>
                </div>
              </div>
            </div>
          </div>

          <div class="quick-actions">
            <div class="card-header">
              <h3>快捷操作</h3>
            </div>
            <div class="actions-grid">
              <div class="action-card" @click="switchTab('booking')">
                <div class="action-icon">
                  <el-icon><Tickets /></el-icon>
                </div>
                <span>代客预订</span>
              </div>
              <div class="action-card" @click="switchTab('scooters')">
                <div class="action-icon">
                  <el-icon><Van /></el-icon>
                </div>
                <span>车辆管理</span>
              </div>
              <div class="action-card" @click="switchTab('map')">
                <div class="action-icon">
                  <el-icon><MapLocation /></el-icon>
                </div>
                <span>车辆地图</span>
              </div>
              <div class="action-card" @click="switchTab('issues')">
                <div class="action-icon">
                  <el-icon><Warning /></el-icon>
                </div>
                <span>故障工单</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 收入报表 -->
        <div v-if="activeTab === 'reports'" class="tab-content">
          <div class="stats-grid">
            <div class="stat-card primary">
              <div class="stat-icon"><el-icon><Money /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">¥{{ totalIncome }}</span>
                <span class="stat-label">总收入</span>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon"><el-icon><Calendar /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">¥{{ weeklyIncome }}</span>
                <span class="stat-label">本周收入</span>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon"><el-icon><Clock /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">¥{{ todayIncome }}</span>
                <span class="stat-label">今日收入</span>
              </div>
            </div>
          </div>
          <div class="chart-row">
            <div class="chart-card">
              <div class="card-header"><h3>各租期收入分布</h3></div>
              <div ref="chartByOptionRef2" class="chart"></div>
            </div>
            <div class="chart-card">
              <div class="card-header"><h3>每日收入趋势</h3></div>
              <div ref="chartDailyRef2" class="chart"></div>
            </div>
          </div>
        </div>

        <!-- 用户管理 -->
        <div v-if="activeTab === 'users'" class="tab-content">
          <div class="content-card">
            <div class="card-header">
              <h3>用户列表</h3>
              <div class="header-actions">
                <el-input v-model="userSearch" placeholder="搜索用户..." clearable style="width: 240px">
                  <template #prefix><el-icon><Search /></el-icon></template>
                </el-input>
              </div>
            </div>
            <el-table :data="filteredUsers" stripe v-loading="loading">
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="username" label="用户名" min-width="140" />
              <el-table-column prop="email" label="邮箱" min-width="200" />
              <el-table-column prop="phone" label="电话" width="150" />
              <el-table-column prop="role" label="角色" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'" size="small">{{ row.role }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="isActive" label="状态" width="90">
                <template #default="{ row }">
                  <el-tag :type="row.isActive ? 'success' : 'info'" size="small">{{ row.isActive ? '活跃' : '禁用' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" fixed="right">
                <template #default="{ row }">
                  <el-button size="small" :type="row.isActive ? 'danger' : 'success'" plain @click="toggleUserStatus(row)">
                    {{ row.isActive ? '禁用' : '启用' }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 车辆管理 -->
        <div v-if="activeTab === 'scooters'" class="tab-content">
          <div class="stats-grid mini">
            <div class="stat-mini available">
              <span class="mini-value">{{ availableCount }}</span>
              <span class="mini-label">可用</span>
            </div>
            <div class="stat-mini inuse">
              <span class="mini-value">{{ inUseCount }}</span>
              <span class="mini-label">使用中</span>
            </div>
            <div class="stat-mini maintenance">
              <span class="mini-value">{{ maintenanceCount }}</span>
              <span class="mini-label">维护中</span>
            </div>
            <div class="stat-mini total">
              <span class="mini-value">{{ scooters.length }}</span>
              <span class="mini-label">车辆总数</span>
            </div>
          </div>
          <div class="content-card">
            <div class="card-header">
              <h3>车辆状态管理</h3>
              <div class="header-actions">
                <el-button type="primary" @click="refreshData">
                  <el-icon><Refresh /></el-icon>刷新
                </el-button>
              </div>
            </div>
            <el-table :data="scooters" stripe v-loading="loading">
              <el-table-column prop="scooterNumber" label="编号" width="130" />
              <el-table-column prop="status" label="状态" width="110">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="batteryLevel" label="电量" width="100">
                <template #default="{ row }">
                  <div class="battery-cell">
                    <span class="battery-val">{{ row.batteryLevel || '—' }}%</span>
                    <div class="battery-bar">
                      <div class="battery-fill" :class="getBatteryClass(row.batteryLevel)" :style="{ width: (row.batteryLevel || 0) + '%' }"></div>
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="location" label="位置" min-width="180" />
              <el-table-column prop="depotId" label="服务点" width="130">
                <template #default="{ row }">{{ getDepotName(row.depotId) }}</template>
              </el-table-column>
              <el-table-column label="操作" width="230" fixed="right">
                <template #default="{ row }">
                  <div class="action-buttons">
                    <el-button size="small" type="success" plain @click="setStatus(row.id, 'AVAILABLE')">可用</el-button>
                    <el-button size="small" type="warning" plain @click="setStatus(row.id, 'IN_USE')">使用中</el-button>
                    <el-button size="small" type="info" plain @click="setStatus(row.id, 'MAINTENANCE')">维护</el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 服务点管理 -->
        <div v-if="activeTab === 'depot'" class="tab-content">
          <div class="content-card">
            <div class="card-header">
              <h3>服务点列表</h3>
              <div class="header-actions">
                <el-button type="primary" @click="refreshData">
                  <el-icon><Refresh /></el-icon>刷新
                </el-button>
              </div>
            </div>
            <el-table :data="depots" stripe v-loading="loading">
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="name" label="名称" min-width="160" />
              <el-table-column prop="address" label="地址" min-width="240" />
              <el-table-column prop="latitude" label="纬度" width="110" />
              <el-table-column prop="longitude" label="经度" width="110" />
              <el-table-column prop="capacity" label="容量" width="80" />
              <el-table-column prop="currentStock" label="当前库存" width="100">
                <template #default="{ row }">
                  <span class="stock-badge" :class="getStockClass(row.currentStock, row.capacity)">
                    {{ row.currentStock }}/{{ row.capacity }}
                  </span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 代客预订 -->
        <div v-if="activeTab === 'booking'" class="tab-content">
          <div class="content-card">
            <div class="card-header">
              <h3>员工代客预订</h3>
            </div>
            <el-form :model="staffForm" label-width="100px" class="staff-form">
              <el-form-item label="选择用户" required>
                <el-select v-model="staffForm.userId" filterable placeholder="搜索并选择用户" style="width: 100%">
                  <el-option v-for="u in users" :key="u.id" :label="`${u.username} (${u.email || '无邮箱'})`" :value="u.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="选择车辆" required>
                <el-select v-model="staffForm.scooterId" filterable placeholder="选择可用车辆" style="width: 100%">
                  <el-option v-for="s in availableScooters" :key="s.id" :label="`${s.scooterNumber} - ${getStatusText(s.status)}`" :value="s.id" />
                </el-select>
              </el-form-item>
              <el-form-item label="租用时长" required>
                <el-select v-model="staffForm.hireOption" style="width: 100%">
                  <el-option label="1 小时" value="1hr" />
                  <el-option label="4 小时" value="4hr" />
                  <el-option label="1 天" value="1day" />
                  <el-option label="1 周" value="1week" />
                </el-select>
              </el-form-item>
              <el-form-item label="开始时间" required>
                <el-date-picker v-model="staffForm.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" size="large" :loading="staffLoading" @click="submitStaffBooking">创建预订订单</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- 价格配置 -->
        <div v-if="activeTab === 'pricing'" class="tab-content">
          <div class="content-card">
            <div class="card-header">
              <h3>价格方案</h3>
            </div>
            <el-table :data="pricingList" stripe>
              <el-table-column prop="hireOption" label="租期代码" width="130" />
              <el-table-column prop="description" label="说明" min-width="200" />
              <el-table-column prop="price" label="价格 (¥)" width="160">
                <template #default="{ row }">
                  <el-input-number v-model="row.price" :min="0" :precision="2" :step="1" size="small" controls-position="right" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="savePricing(row)">保存</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="content-card">
            <div class="card-header">
              <h3>租用选项</h3>
            </div>
            <el-table :data="hireOptions" stripe>
              <el-table-column prop="code" label="代码" width="110" />
              <el-table-column prop="label" label="名称" min-width="180" />
              <el-table-column prop="durationMinutes" label="时长(分钟)" width="130" />
              <el-table-column prop="price" label="价格" width="160">
                <template #default="{ row }">
                  <el-input-number v-model="row.price" :min="0" :precision="2" :step="1" size="small" controls-position="right" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="saveHireOption(row)">保存</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 折扣管理 -->
        <div v-if="activeTab === 'discount'" class="tab-content">
          <div class="content-card">
            <div class="card-header">
              <h3>折扣列表</h3>
              <div class="header-actions">
                <el-button type="primary" @click="refreshData">
                  <el-icon><Refresh /></el-icon>刷新
                </el-button>
              </div>
            </div>
            <el-table :data="discounts" stripe v-loading="loading">
              <el-table-column prop="code" label="折扣码" width="140" />
              <el-table-column prop="description" label="描述" min-width="220" />
              <el-table-column prop="discountType" label="类型" width="110">
                <template #default="{ row }">
                  <el-tag size="small">{{ row.discountType === 'PERCENTAGE' ? '百分比' : '固定金额' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="discountValue" label="优惠值" width="100">
                <template #default="{ row }">
                  {{ row.discountType === 'PERCENTAGE' ? row.discountValue + '%' : '¥' + row.discountValue }}
                </template>
              </el-table-column>
              <el-table-column prop="minBookingValue" label="最低消费" width="110">
                <template #default="{ row }">¥{{ row.minBookingValue || 0 }}</template>
              </el-table-column>
              <el-table-column prop="usageLimit" label="限制次数" width="100" />
              <el-table-column prop="usageCount" label="已使用" width="90" />
              <el-table-column prop="isActive" label="状态" width="90">
                <template #default="{ row }">
                  <el-tag :type="row.isActive ? 'success' : 'info'" size="small">{{ row.isActive ? '启用' : '禁用' }}</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 车辆地图 -->
        <div v-if="activeTab === 'map'" class="tab-content">
          <div class="stats-grid mini">
            <div class="stat-mini available">
              <span class="mini-value">{{ availableCount }}</span>
              <span class="mini-label">可用</span>
            </div>
            <div class="stat-mini inuse">
              <span class="mini-value">{{ inUseCount }}</span>
              <span class="mini-label">使用中</span>
            </div>
            <div class="stat-mini maintenance">
              <span class="mini-value">{{ maintenanceCount }}</span>
              <span class="mini-label">维护中</span>
            </div>
          </div>
          <div class="content-card map-card">
            <div class="card-header">
              <h3>车辆位置地图</h3>
              <div class="header-actions">
                <el-button type="primary" @click="refreshData">
                  <el-icon><Refresh /></el-icon>刷新
                </el-button>
              </div>
            </div>
            <div class="map-container">
              <div id="adminMap" class="admin-map"></div>
              <div class="map-legend">
                <div class="legend-item">
                  <span class="legend-dot available"></span>
                  <span>可用</span>
                </div>
                <div class="legend-item">
                  <span class="legend-dot inuse"></span>
                  <span>使用中</span>
                </div>
                <div class="legend-item">
                  <span class="legend-dot maintenance"></span>
                  <span>维护中</span>
                </div>
              </div>
              <div class="map-stats">
                <div class="map-stat">
                  <span class="map-stat-value">{{ availableCount }}</span>
                  <span class="map-stat-label">可用车辆</span>
                </div>
                <div class="map-stat">
                  <span class="map-stat-value">{{ inUseCount }}</span>
                  <span class="map-stat-label">使用中</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 故障工单 -->
        <div v-if="activeTab === 'issues'" class="tab-content">
          <div class="stats-grid mini">
            <div class="stat-mini open">
              <span class="mini-value">{{ openIssues }}</span>
              <span class="mini-label">待处理</span>
            </div>
            <div class="stat-mini progress">
              <span class="mini-value">{{ inProgressIssues }}</span>
              <span class="mini-label">处理中</span>
            </div>
            <div class="stat-mini resolved">
              <span class="mini-value">{{ resolvedIssues }}</span>
              <span class="mini-label">已解决</span>
            </div>
          </div>
          <div class="content-card">
            <div class="card-header">
              <h3>车辆故障报告</h3>
              <div class="header-actions">
                <el-button @click="refreshData">
                  <el-icon><Refresh /></el-icon>刷新
                </el-button>
              </div>
            </div>
            <el-table :data="issueReports" stripe v-loading="loading">
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="scooterId" label="车辆ID" width="100" />
              <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
              <el-table-column prop="priority" label="优先级" width="100">
                <template #default="{ row }">
                  <el-tag :type="getPriorityType(row.priority)" size="small">{{ row.priority }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="110">
                <template #default="{ row }">
                  <el-tag :type="getIssueStatusType(row.status)" size="small">{{ row.status }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="时间" width="160" />
              <el-table-column label="操作" width="100" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" link @click="openProcess(row)">处理</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 用户反馈 -->
        <div v-if="activeTab === 'feedback'" class="tab-content">
          <div class="content-card">
            <div class="card-header">
              <h3>用户反馈</h3>
              <div class="header-actions">
                <el-button type="primary" @click="refreshData">
                  <el-icon><Refresh /></el-icon>刷新
                </el-button>
              </div>
            </div>
            <el-table :data="feedbacks" stripe v-loading="loading">
              <el-table-column prop="id" label="ID" width="70" />
              <el-table-column prop="description" label="内容" min-width="240" show-overflow-tooltip />
              <el-table-column prop="priority" label="优先级" width="100">
                <template #default="{ row }">
                  <el-tag :type="getPriorityType(row.priority)" size="small">{{ row.priority }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="110" />
              <el-table-column prop="adminResponse" label="回复" min-width="180" show-overflow-tooltip />
              <el-table-column label="操作" width="100" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" link @click="openProcess(row)">处理</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 用户分析 -->
        <div v-if="activeTab === 'userAnalysis'" class="tab-content">
          <div class="stats-grid">
            <div class="stat-card success">
              <div class="stat-icon"><el-icon><User /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ totalUsers }}</span>
                <span class="stat-label">总用户数</span>
              </div>
            </div>
            <div class="stat-card primary">
              <div class="stat-icon"><el-icon><UserFilled /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ todayNewUsers }}</span>
                <span class="stat-label">今日新增</span>
              </div>
            </div>
            <div class="stat-card warning">
              <div class="stat-icon"><el-icon><TrendCharts /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ weekNewUsers }}</span>
                <span class="stat-label">本周新增</span>
              </div>
            </div>
          </div>
          <div class="overview-row">
            <div class="chart-card">
              <div class="card-header"><h3>用户增长趋势</h3></div>
              <div ref="chartUserGrowthRef" class="chart"></div>
            </div>
            <div class="chart-card">
              <div class="card-header"><h3>用户活跃度 TOP 10</h3></div>
              <div class="user-ranking">
                <div v-for="(user, idx) in topActiveUsers" :key="user.id" class="ranking-item">
                  <span class="ranking-num" :class="{ gold: idx === 0, silver: idx === 1, bronze: idx === 2 }">{{ idx + 1 }}</span>
                  <span class="ranking-name">{{ user.username }}</span>
                  <span class="ranking-value">{{ user.bookingCount }} 次</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 订单分析 -->
        <div v-if="activeTab === 'orderAnalysis'" class="tab-content">
          <div class="stats-grid">
            <div class="stat-card primary">
              <div class="stat-icon"><el-icon><Tickets /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ totalBookings }}</span>
                <span class="stat-label">总订单数</span>
              </div>
            </div>
            <div class="stat-card success">
              <div class="stat-icon"><el-icon><CircleCheck /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ completedBookings }}</span>
                <span class="stat-label">已完成</span>
              </div>
            </div>
            <div class="stat-card warning">
              <div class="stat-icon"><el-icon><Clock /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">{{ cancelledBookings }}</span>
                <span class="stat-label">已取消</span>
              </div>
            </div>
            <div class="stat-card info">
              <div class="stat-icon"><el-icon><Coin /></el-icon></div>
              <div class="stat-info">
                <span class="stat-value">¥{{ avgOrderValue }}</span>
                <span class="stat-label">平均订单金额</span>
              </div>
            </div>
          </div>
          <div class="overview-row">
            <div class="chart-card">
              <div class="card-header"><h3>订单状态分布</h3></div>
              <div ref="chartOrderStatusRef" class="chart"></div>
            </div>
            <div class="chart-card">
              <div class="card-header"><h3>热门预订时段</h3></div>
              <div ref="chartPeakHoursRef" class="chart"></div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 处理反馈弹窗 -->
    <el-dialog v-model="processVisible" title="处理问题" width="520px" class="process-dialog">
      <el-form v-if="processRow" :model="processForm" label-width="100px">
        <el-form-item label="处理状态">
          <el-select v-model="processForm.status" style="width: 100%">
            <el-option label="待处理 (OPEN)" value="OPEN" />
            <el-option label="处理中 (IN_PROGRESS)" value="IN_PROGRESS" />
            <el-option label="已解决 (RESOLVED)" value="RESOLVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="processForm.priority" style="width: 100%">
            <el-option label="低 (LOW)" value="LOW" />
            <el-option label="中 (MEDIUM)" value="MEDIUM" />
            <el-option label="高 (HIGH)" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="管理员回复">
          <el-input v-model="processForm.adminResponse" type="textarea" :rows="4" placeholder="请输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processVisible = false">取消</el-button>
        <el-button type="primary" :loading="processLoading" @click="submitProcess">提交处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import {
  DataLine, TrendCharts, User, Van, LocationFilled, Tickets, Money, Sell,
  Warning, ChatDotRound, SwitchButton, Refresh, HomeFilled, Top,
  Calendar, Clock, Search, MapLocation, Tools, UserFilled, CircleCheck, Coin
} from '@element-plus/icons-vue'
import {
  adminCreateBooking,
  getWeeklyIncomeReport,
  getDailyIncomeReport,
  getAllFeedbacks,
  processFeedback,
  getAdminPricing,
  updateAdminPricing,
  getStatisticsOverview,
  getStatisticsUsers,
  getStatisticsBookings,
  getStatisticsScooters,
  getStatisticsUserGrowth,
  getStatisticsTopUsers,
  getStatisticsBookingStatus,
  getStatisticsPeakHours
} from '@/api/admin'
import { listUsers, updateUserStatus } from '@/api/user'
import { getScooters, updateScooterStatus } from '@/api/scooter'
import { listHireOptions, updateHireOption } from '@/api/hireOptions'
import { getDepots } from '@/api/depot'
import { listDiscounts } from '@/api/discount'
import request from '@/utils/request'

const router = useRouter()

// 状态
const activeTab = ref('overview')
const loading = ref(false)
const adminName = ref('管理员')
const userSearch = ref('')

// 数据
const users = ref([])
const scooters = ref([])
const depots = ref([])
const pricingList = ref([])
const hireOptions = ref([])
const feedbacks = ref([])
const issueReports = ref([])
const discounts = ref([])

// 统计数据
const totalIncome = ref('0.00')
const weeklyIncome = ref('0.00')
const todayIncome = ref('0.00')
const totalUsers = ref(0)
const activeBookings = ref(0)
const todayNewUsers = ref(0)
const weekNewUsers = ref(0)
const usageRate = ref(0)
const avgRideDuration = ref(0)
const completionRate = ref(0)
const totalBookings = ref(0)
const completedBookings = ref(0)
const cancelledBookings = ref(0)
const avgOrderValue = ref('0.00')

// 图表
const chartByOptionRef = ref(null)
const chartDailyRef = ref(null)
const chartByOptionRef2 = ref(null)
const chartDailyRef2 = ref(null)
const chartUserGrowthRef = ref(null)
const chartOrderStatusRef = ref(null)
const chartPeakHoursRef = ref(null)
let chartOption = null
let chartDaily = null
let chartUserGrowth = null
let chartOrderStatus = null
let chartPeakHours = null

// 地图
let adminMap = null

// 用户排名
const topActiveUsers = ref([])

// 代客预订
const staffForm = ref({ userId: null, scooterId: null, hireOption: '1hr', startTime: new Date() })
const staffLoading = ref(false)

// 处理反馈
const processVisible = ref(false)
const processRow = ref(null)
const processForm = ref({ status: 'OPEN', priority: 'LOW', adminResponse: '' })
const processLoading = ref(false)

// 统计数据计算
const availableCount = computed(() => scooters.value.filter(s => s.status === 'AVAILABLE').length)
const inUseCount = computed(() => scooters.value.filter(s => s.status === 'IN_USE').length)
const maintenanceCount = computed(() => scooters.value.filter(s => s.status === 'MAINTENANCE').length)

const pendingIssues = computed(() => issueReports.value.filter(i => i.status === 'OPEN').length)
const openIssues = computed(() => issueReports.value.filter(i => i.status === 'OPEN').length)
const inProgressIssues = computed(() => issueReports.value.filter(i => i.status === 'IN_PROGRESS').length)
const resolvedIssues = computed(() => issueReports.value.filter(i => i.status === 'RESOLVED').length)

const filteredUsers = computed(() => {
  if (!userSearch.value) return users.value
  const kw = userSearch.value.toLowerCase()
  return users.value.filter(u =>
    (u.username && u.username.toLowerCase().includes(kw)) ||
    (u.email && u.email.toLowerCase().includes(kw))
  )
})

const availableScooters = computed(() => scooters.value.filter(s => s.status === 'AVAILABLE'))

// 预警计算
const lowBatteryScooters = computed(() => scooters.value.filter(s => s.batteryLevel && s.batteryLevel < 20))
const maintenanceNeeded = computed(() => scooters.value.filter(s => s.status === 'MAINTENANCE'))
const lowStockDepots = computed(() => depots.value.filter(d => {
  const ratio = d.currentStock / d.capacity
  return ratio < 0.3
}))

// 工具函数
const getStatusType = (status) => {
  const map = { AVAILABLE: 'success', IN_USE: 'warning', MAINTENANCE: 'info', RETIRED: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { AVAILABLE: '可用', IN_USE: '使用中', MAINTENANCE: '维护中', RETIRED: '退役' }
  return map[status] || status
}

const getBatteryClass = (level) => {
  if (!level) return 'low'
  if (level >= 60) return 'high'
  if (level >= 30) return 'medium'
  return 'low'
}

const getStockClass = (current, capacity) => {
  const ratio = current / capacity
  if (ratio > 0.5) return 'good'
  if (ratio > 0.2) return 'warning'
  return 'danger'
}

const getDepotName = (id) => {
  const depot = depots.value.find(d => d.id === id)
  return depot ? depot.name : '—'
}

const getPriorityType = (priority) => {
  const map = { HIGH: 'danger', MEDIUM: 'warning', LOW: 'info' }
  return map[priority] || 'info'
}

const getIssueStatusType = (status) => {
  const map = { OPEN: 'warning', IN_PROGRESS: 'primary', RESOLVED: 'success' }
  return map[status] || 'info'
}

// Tab切换
const switchTab = async (tab) => {
  activeTab.value = tab
  await nextTick()
  if (tab === 'reports') renderReportCharts()
  else if (tab === 'userAnalysis') renderUserAnalysisCharts()
  else if (tab === 'orderAnalysis') renderOrderAnalysisCharts()
  else if (tab === 'map') initAdminMap()
}

// 刷新数据
const refreshData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadUsers(),
      loadScooters(),
      loadDepots(),
      loadPricing(),
      loadHireOptions(),
      loadFeedbacks(),
      loadIssues(),
      // loadDiscounts(), // TODO: 后端接口修复后再启用
      loadReports()
    ])
  } finally {
    loading.value = false
  }
}

// 加载数据
const loadUsers = async () => {
  try {
    const res = await listUsers()
    users.value = Array.isArray(res) ? res.filter(u => u.role !== 'ADMIN') : []
    totalUsers.value = users.value.length
  } catch (e) { console.error(e) }
}

const loadScooters = async () => {
  try {
    const res = await getScooters()
    scooters.value = Array.isArray(res) ? res : []
  } catch (e) { console.error(e) }
}

const loadDepots = async () => {
  try {
    const res = await getDepots()
    depots.value = Array.isArray(res) ? res : []
  } catch (e) { console.error(e) }
}

const loadPricing = async () => {
  try {
    const res = await getAdminPricing()
    pricingList.value = Array.isArray(res) ? res.map(r => ({ ...r, price: Number(r.price) })) : []
  } catch (e) { console.error(e) }
}

const loadHireOptions = async () => {
  try {
    const res = await listHireOptions()
    hireOptions.value = Array.isArray(res) ? res.map(r => ({ ...r, price: Number(r.price) })) : []
  } catch (e) { console.error(e) }
}

const loadFeedbacks = async () => {
  try {
    const res = await getAllFeedbacks()
    feedbacks.value = Array.isArray(res) ? res : []
  } catch (e) { console.error(e) }
}

const loadIssues = async () => {
  try {
    const res = await request({ url: '/api/issues', method: 'get' })
    issueReports.value = Array.isArray(res) ? res : []
  } catch (e) { console.error(e) }
}

const loadDiscounts = async () => {
  try {
    const res = await listDiscounts()
    discounts.value = Array.isArray(res) ? res : []
  } catch (e) { console.error(e) }
}

const loadReports = async () => {
  try {
    // 使用新的统计 API
    const overview = await getStatisticsOverview()
    const userStats = await getStatisticsUsers()

    // 填充概览数据
    totalIncome.value = (overview?.totalIncome || 0).toFixed(2)
    weeklyIncome.value = (overview?.weekIncome || 0).toFixed(2)
    todayIncome.value = (overview?.todayIncome || 0).toFixed(2)
    totalUsers.value = overview?.totalUsers || 0
    activeBookings.value = overview?.activeBookings || 0
    usageRate.value = overview?.usageRate || 0
    avgRideDuration.value = overview?.avgRideDuration || 0
    completionRate.value = overview?.completionRate || 0

    // 用户统计
    todayNewUsers.value = userStats?.todayNewUsers || 0
    weekNewUsers.value = userStats?.weekNewUsers || 0

    // 订单统计
    const bookingStats = await getStatisticsBookings()
    totalBookings.value = bookingStats?.totalBookings || 0
    completedBookings.value = bookingStats?.completedBookings || 0
    cancelledBookings.value = bookingStats?.cancelledBookings || 0
    avgOrderValue.value = (bookingStats?.avgOrderValue || 0).toFixed(2)

    // 活跃用户排行
    const topUsersRes = await getStatisticsTopUsers()
    topActiveUsers.value = topUsersRes?.topUsers || []

    // 获取收入报表数据
    const weekly = await getWeeklyIncomeReport()
    const daily = await getDailyIncomeReport()

    await nextTick()
    renderReportCharts()
  } catch (e) {
    console.error('加载统计数据失败:', e)
    // 备用：使用旧接口
    try {
      const weekly = await getWeeklyIncomeReport()
      const daily = await getDailyIncomeReport()
      const byOpt = weekly?.incomeByHireOption || {}
      const optValues = Object.values(byOpt)
      const total = optValues.reduce((sum, v) => sum + Number(v || 0), 0)
      totalIncome.value = total.toFixed(2)
      weeklyIncome.value = total.toFixed(2)
      const today = new Date().toISOString().split('T')[0]
      const dailyMap = daily?.dailyIncome || {}
      todayIncome.value = (Number(dailyMap[today]) || 0).toFixed(2)
      activeBookings.value = inUseCount.value
      renderReportCharts()
    } catch (e2) {
      console.error('备用加载也失败:', e2)
    }
  }
}

// 渲染图表
const renderReportCharts = () => {
  getWeeklyIncomeReport().then(weekly => {
    getDailyIncomeReport().then(daily => {
      const byOpt = weekly?.incomeByHireOption || {}
      const dailyMap = daily?.dailyIncome || {}

      ;[chartByOptionRef.value, chartByOptionRef2.value].forEach(ref => {
        if (ref) {
          const chart = echarts.getInstanceByDom(ref) || echarts.init(ref)
          chart.setOption({
            color: ['#1e3a5f', '#3b5998', '#6b9ac4', '#94a3b8'],
            tooltip: { trigger: 'axis' },
            xAxis: { type: 'category', data: Object.keys(byOpt), axisLabel: { rotate: 30 } },
            yAxis: { type: 'value', name: '金额 (¥)' },
            series: [{ type: 'bar', data: Object.values(byOpt), itemStyle: { borderRadius: [4, 4, 0, 0] } }]
          })
        }
      })

      ;[chartDailyRef.value, chartDailyRef2.value].forEach(ref => {
        if (ref) {
          const chart = echarts.getInstanceByDom(ref) || echarts.init(ref)
          const days = Object.keys(dailyMap).sort()
          chart.setOption({
            color: ['#6b9ac4'],
            tooltip: { trigger: 'axis' },
            xAxis: { type: 'category', data: days, axisLabel: { rotate: 30 } },
            yAxis: { type: 'value', name: '金额 (¥)' },
            series: [{
              type: 'line',
              smooth: true,
              areaStyle: { opacity: 0.3 },
              data: days.map(d => dailyMap[d])
            }]
          })
        }
      })
    })
  })
}

// 用户分析图表
const renderUserAnalysisCharts = () => {
  nextTick(() => {
    if (chartUserGrowthRef.value) {
      const chart = echarts.getInstanceByDom(chartUserGrowthRef.value) || echarts.init(chartUserGrowthRef.value)

      // 获取用户增长数据
      getStatisticsUserGrowth().then(res => {
        const dailyData = res?.dailyNewUsers || {}
        const days = Object.keys(dailyData)
        const data = Object.values(dailyData)

        chart.setOption({
          color: ['#1e3a5f'],
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: days },
          yAxis: { type: 'value', name: '用户数' },
          series: [{
            type: 'bar',
            data,
            itemStyle: { borderRadius: [4, 4, 0, 0] }
          }]
        })
      }).catch(() => {
        // 备用模拟数据
        const days = Array.from({ length: 7 }, (_, i) => {
          const d = new Date()
          d.setDate(d.getDate() - (6 - i))
          return `${d.getMonth() + 1}/${d.getDate()}`
        })
        const data = Array.from({ length: 7 }, () => Math.floor(Math.random() * 10) + 1)
        chart.setOption({
          color: ['#1e3a5f'],
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: days },
          yAxis: { type: 'value', name: '用户数' },
          series: [{ type: 'bar', data, itemStyle: { borderRadius: [4, 4, 0, 0] } }]
        })
      })
    }
  })
}

// 订单分析图表
const renderOrderAnalysisCharts = () => {
  nextTick(() => {
    // 订单状态饼图
    if (chartOrderStatusRef.value) {
      const chart = echarts.getInstanceByDom(chartOrderStatusRef.value) || echarts.init(chartOrderStatusRef.value)
      chart.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
          label: { show: true, formatter: '{b}: {c} ({d}%)' },
          data: [
            { value: completedBookings.value, name: '已完成', itemStyle: { color: '#2d8a4e' } },
            { value: activeBookings.value, name: '进行中', itemStyle: { color: '#c4880c' } },
            { value: cancelledBookings.value, name: '已取消', itemStyle: { color: '#d14545' } }
          ]
        }]
      })
    }

    // 热门时段图
    if (chartPeakHoursRef.value) {
      const chart = echarts.getInstanceByDom(chartPeakHoursRef.value) || echarts.init(chartPeakHoursRef.value)

      getStatisticsPeakHours().then(res => {
        const peakData = res?.peakHours || {}
        const hours = Object.keys(peakData)
        const data = Object.values(peakData)
        chart.setOption({
          color: ['#6b9ac4'],
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: hours },
          yAxis: { type: 'value', name: '订单数' },
          series: [{ type: 'bar', data, itemStyle: { borderRadius: [4, 4, 0, 0] } }]
        })
      }).catch(() => {
        // 备用模拟数据
        const hours = ['8时', '10时', '12时', '14时', '16时', '18时', '20时']
        const data = [12, 25, 18, 15, 22, 30, 20]
        chart.setOption({
          color: ['#6b9ac4'],
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: hours },
          yAxis: { type: 'value', name: '订单数' },
          series: [{ type: 'bar', data, itemStyle: { borderRadius: [4, 4, 0, 0] } }]
        })
      })
    }
  })
}

// 初始化管理地图
const initAdminMap = () => {
  nextTick(() => {
    const mapEl = document.getElementById('adminMap')
    if (!mapEl) return

    // 简单地图实现（实际项目可集成高德/百度地图）
    const defaultCenter = [116.397428, 39.90923] // 北京
    const defaultZoom = 13

    // 使用简单的 SVG 模拟地图效果
    mapEl.innerHTML = `
      <div class="simple-map">
        <div class="map-placeholder">
          <el-icon size="48"><MapLocation /></el-icon>
          <p>车辆分布地图</p>
          <p class="map-hint">共 ${scooters.value.length} 辆车</p>
          <div class="map-markers">
            ${scooters.value.slice(0, 20).map((s, i) => {
              const x = 10 + (i % 5) * 18
              const y = 15 + Math.floor(i / 5) * 20
              const color = s.status === 'AVAILABLE' ? '#2d8a4e' : s.status === 'IN_USE' ? '#c4880c' : '#94a3b8'
              return `<div class="marker" style="left: ${x}%; top: ${y}%; background: ${color};" title="${s.scooterNumber}"></div>`
            }).join('')}
          </div>
        </div>
      </div>
    `
  })
}

// 用户状态切换
const toggleUserStatus = async (row) => {
  try {
    await updateUserStatus(row.id, !row.isActive)
    row.isActive = !row.isActive
    ElMessage.success(`用户已${row.isActive ? '启用' : '禁用'}`)
  } catch (e) { console.error(e) }
}

// 更新车辆状态
const setStatus = async (id, status) => {
  try {
    await updateScooterStatus(id, status)
    ElMessage.success('状态已更新')
    await loadScooters()
  } catch (e) { console.error(e) }
}

// 代客预订
const submitStaffBooking = async () => {
  if (!staffForm.value.userId || !staffForm.value.scooterId || !staffForm.value.startTime) {
    ElMessage.warning('请填写完整信息')
    return
  }
  staffLoading.value = true
  try {
    await adminCreateBooking({
      userId: staffForm.value.userId,
      scooterId: staffForm.value.scooterId,
      hireOption: staffForm.value.hireOption,
      startTime: staffForm.value.startTime.toISOString()
    })
    ElMessage.success('代客订单创建成功')
    staffForm.value = { userId: null, scooterId: null, hireOption: '1hr', startTime: new Date() }
  } catch (e) { console.error(e) } finally {
    staffLoading.value = false
  }
}

// 保存价格
const savePricing = async (row) => {
  try {
    await updateAdminPricing({ id: row.id, hireOption: row.hireOption, price: row.price, description: row.description })
    ElMessage.success('价格已保存')
  } catch (e) { console.error(e) }
}

const saveHireOption = async (row) => {
  try {
    await updateHireOption(row.id, { code: row.code, label: row.label, durationMinutes: row.durationMinutes, price: row.price })
    ElMessage.success('租用选项已保存')
  } catch (e) { console.error(e) }
}

// 处理反馈
const openProcess = (row) => {
  processRow.value = row
  processForm.value = { status: row.status || 'OPEN', priority: row.priority || 'LOW', adminResponse: row.adminResponse || '' }
  processVisible.value = true
}

const submitProcess = async () => {
  if (!processRow.value) return
  processLoading.value = true
  try {
    await processFeedback(processRow.value.id, { ...processForm.value })
    ElMessage.success('处理结果已保存')
    processVisible.value = false
    await loadFeedbacks()
  } catch (e) { console.error(e) } finally {
    processLoading.value = false
  }
}

// 退出
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('已退出登录')
  router.push('/admin/login')
}

// 初始化
onMounted(async () => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      const user = JSON.parse(userStr)
      adminName.value = user.username || '管理员'
    } catch (e) {}
  }

  await refreshData()

  window.addEventListener('resize', () => {
    chartOption?.resize()
    chartDaily?.resize()
  })
})

onUnmounted(() => {
  chartOption?.dispose()
  chartDaily?.dispose()
})
</script>

<style scoped>
/* 布局 */
.admin-container {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(180deg, #e8eef5 0%, #d6e0eb 100%);
}

/* 侧边栏 */
.sidebar {
  width: 260px;
  background: white;
  border-right: 1px solid #d6e0eb;
  display: flex;
  flex-direction: column;
  position: fixed;
  height: 100vh;
  z-index: 100;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #e8eef5;
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon svg {
  width: 24px;
  height: 24px;
  color: white;
}

.logo-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-name {
  font-size: 17px;
  font-weight: 800;
  color: #1e3a5f;
}

.brand-tag {
  font-size: 11px;
  color: #5a7a9a;
}

/* 导航 */
.sidebar-nav {
  flex: 1;
  padding: 16px 12px;
  overflow-y: auto;
}

.nav-section {
  margin-bottom: 24px;
}

.nav-label {
  display: block;
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  padding: 0 12px;
  margin-bottom: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 10px;
  color: #5a7a9a;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
}

.nav-item:hover {
  background: #f0f4f8;
  color: #1e3a5f;
}

.nav-item.active {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  color: white;
}

.nav-item .el-icon {
  font-size: 18px;
}

.badge {
  margin-left: auto;
  background: #d14545;
  color: white;
  font-size: 11px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 10px;
}

/* 侧边栏底部 */
.sidebar-footer {
  padding: 16px;
  border-top: 1px solid #e8eef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.admin-avatar {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  color: white;
  font-weight: 700;
}

.admin-detail {
  display: flex;
  flex-direction: column;
}

.admin-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e3a5f;
}

.admin-role {
  font-size: 11px;
  color: #94a3b8;
}

.logout-btn {
  padding: 8px;
}

/* 主内容区 */
.main-area {
  flex: 1;
  margin-left: 260px;
  min-height: 100vh;
}

/* 内容区域 */
.content-wrapper {
  padding: 24px 28px;
}

.tab-content {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stats-grid.mini {
  grid-template-columns: repeat(4, 1fr);
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.08);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
}

.stat-card.primary::before { background: linear-gradient(180deg, #1e3a5f, #3b5998); }
.stat-card.success::before { background: linear-gradient(180deg, #2d8a4e, #5cb885); }
.stat-card.warning::before { background: linear-gradient(180deg, #c4880c, #e6a82d); }
.stat-card.info::before { background: linear-gradient(180deg, #6b9ac4, #94b8d4); }

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-card.primary .stat-icon { background: rgba(30, 58, 95, 0.1); color: #1e3a5f; }
.stat-card.success .stat-icon { background: rgba(45, 138, 78, 0.1); color: #2d8a4e; }
.stat-card.warning .stat-icon { background: rgba(196, 136, 12, 0.1); color: #c4880c; }
.stat-card.info .stat-icon { background: rgba(107, 154, 196, 0.1); color: #6b9ac4; }

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 26px;
  font-weight: 800;
  color: #1e3a5f;
}

.stat-label {
  font-size: 13px;
  color: #94a3b8;
}

.stat-trend {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
}

.stat-trend.up { color: #2d8a4e; }
.stat-trend.down { color: #d14545; }

/* 小型统计 */
.stat-mini {
  background: white;
  border-radius: 12px;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 2px 12px rgba(30, 58, 95, 0.06);
}

.stat-mini::before {
  content: '';
  width: 100%;
  height: 4px;
  border-radius: 2px;
  margin-bottom: 12px;
}

.stat-mini.available::before { background: #2d8a4e; }
.stat-mini.inuse::before { background: #5a7a9a; }
.stat-mini.maintenance::before { background: #c4880c; }
.stat-mini.total::before { background: #1e3a5f; }
.stat-mini.open::before { background: #c4880c; }
.stat-mini.progress::before { background: #3b5998; }
.stat-mini.resolved::before { background: #2d8a4e; }

.mini-value {
  font-size: 28px;
  font-weight: 800;
  color: #1e3a5f;
}

.mini-label {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 4px;
}

/* 图表卡片 */
.overview-row, .chart-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e3a5f;
}

.chart {
  height: 280px;
  width: 100%;
}

/* 快捷操作 */
.quick-actions {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.08);
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px 16px;
  background: #f8fafc;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-card:hover {
  background: linear-gradient(135deg, #1e3a5f 0%, #3b5998 100%);
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(30, 58, 95, 0.2);
}

.action-card:hover span,
.action-card:hover .action-icon {
  color: white;
}

.action-icon {
  width: 48px;
  height: 48px;
  background: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #1e3a5f;
  box-shadow: 0 2px 8px rgba(30, 58, 95, 0.1);
  transition: all 0.3s;
}

.action-card span {
  font-size: 14px;
  font-weight: 600;
  color: #5a7a9a;
  transition: all 0.3s;
}

/* 内容卡片 */
.content-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(30, 58, 95, 0.08);
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 表单 */
.staff-form {
  max-width: 480px;
}

/* 电池样式 */
.battery-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.action-buttons {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

/* 运营指标网格 */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  padding: 8px 0;
}

.metric-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
}

.metric-label {
  font-size: 13px;
  color: #94a3b8;
}

.metric-value {
  font-size: 20px;
  font-weight: 700;
  color: #1e3a5f;
}

/* 预警列表 */
.alert-list {
  max-height: 280px;
  overflow-y: auto;
}

.no-alert {
  text-align: center;
  padding: 40px;
  color: #94a3b8;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: 10px;
  margin-bottom: 8px;
  font-size: 13px;
}

.alert-item.danger {
  background: #fde8e8;
  color: #d14545;
}

.alert-item.warning {
  background: #fef7e6;
  color: #c4880c;
}

.alert-item.info {
  background: #e8f4fd;
  color: #3b5998;
}

/* 统计子项 */
.stat-sub {
  display: flex;
  gap: 12px;
  margin-top: 4px;
  font-size: 12px;
  color: #94a3b8;
}

/* 地图样式 */
.map-card {
  padding: 0;
}

.map-card .card-header {
  padding: 20px 24px;
}

.map-container {
  position: relative;
}

.admin-map {
  height: 450px;
  background: #f0f4f8;
  border-radius: 0 0 16px 16px;
}

.simple-map {
  width: 100%;
  height: 100%;
}

.map-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  position: relative;
}

.map-placeholder p {
  margin: 8px 0 0;
}

.map-hint {
  font-size: 13px;
  color: #6b9ac4;
}

.map-markers {
  position: absolute;
  width: 100%;
  height: 80%;
  top: 10%;
}

.marker {
  position: absolute;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  cursor: pointer;
  transition: transform 0.2s;
  box-shadow: 0 2px 6px rgba(0,0,0,0.3);
}

.marker:hover {
  transform: translate(-50%, -50%) scale(1.4);
}

.map-legend {
  position: absolute;
  top: 16px;
  right: 16px;
  background: white;
  padding: 12px 16px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.legend-dot.available { background: #2d8a4e; }
.legend-dot.inuse { background: #c4880c; }
.legend-dot.maintenance { background: #94a3b8; }

.map-stats {
  position: absolute;
  bottom: 16px;
  left: 16px;
  background: white;
  padding: 12px 20px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  display: flex;
  gap: 24px;
}

.map-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.map-stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1e3a5f;
}

.map-stat-label {
  font-size: 12px;
  color: #94a3b8;
}

/* 用户排名 */
.user-ranking {
  max-height: 280px;
  overflow-y: auto;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f4f8;
}

.ranking-item:last-child {
  border-bottom: none;
}

.ranking-num {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #f0f4f8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 13px;
  color: #5a7a9a;
}

.ranking-num.gold { background: linear-gradient(135deg, #ffd700, #ffb347); color: #8b6914; }
.ranking-num.silver { background: linear-gradient(135deg, #c0c0c0, #a8a8a8); color: #5a5a5a; }
.ranking-num.bronze { background: linear-gradient(135deg, #cd7f32, #b87333); color: #5a3a1a; }

.ranking-name {
  flex: 1;
  font-weight: 500;
  color: #1e3a5f;
}

.ranking-value {
  font-weight: 600;
  color: #6b9ac4;
}

.action-buttons .el-button {
  margin: 2px 0;
}

.battery-val {
  font-size: 13px;
  font-weight: 600;
}

.battery-bar {
  width: 60px;
  height: 6px;
  background: #e8eef5;
  border-radius: 3px;
  overflow: hidden;
}

.battery-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s;
}

.battery-fill.high { background: #2d8a4e; }
.battery-fill.medium { background: #c4880c; }
.battery-fill.low { background: #d14545; }

/* 库存徽章 */
.stock-badge {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

.stock-badge.good { background: #e6f4ea; color: #2d8a4e; }
.stock-badge.warning { background: #fef7e6; color: #c4880c; }
.stock-badge.danger { background: #fde8e8; color: #d14545; }

/* 弹窗 */
.process-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid #e8eef5;
  padding-bottom: 16px;
}

.process-dialog :deep(.el-dialog__title) {
  font-weight: 700;
  color: #1e3a5f;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .actions-grid { grid-template-columns: repeat(2, 1fr); }
  .overview-row, .chart-row { grid-template-columns: 1fr; }
}
</style>
