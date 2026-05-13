package com.example.demo.service.impl;

import com.example.demo.entity.IssueReport;
import com.example.demo.mapper.IssueReportMapper;
import com.example.demo.service.IssueReportService;
import com.example.demo.vo.IssueReportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 问题报告服务实现类
 * 实现问题报告相关的业务逻辑（ID13/14：报告问题）
 * ID13: 根据描述自动设置优先级 - 高危问题升级为HIGH，轻微问题降为LOW
 */
@Service
public class IssueReportServiceImpl implements IssueReportService {

    private static final Logger log = LoggerFactory.getLogger(IssueReportServiceImpl.class);

    @Autowired
    private IssueReportMapper issueReportMapper;

    // 高优先级关键词（危险/安全问题 - 需要立即处理）
    private static final String[] HIGH_PRIORITY_KEYWORDS = {
        // 英文危险关键词
        "dangerous", "safety", "accident", "brake failure", "brake not working",
        "brake broken", "no brakes", "broken brake", "won't stop", "cannot stop",
        "won't start", "cannot start", "battery fire", "battery smoking",
        "battery explosion", "smoke", "smoking", "on fire", "catching fire",
        "emergency", "stuck", "uncontrollable", "shaking violently",
        "vibrating violently", "losing control", "slipping", "wet floor",
        "hit by car", "collided", "crash", "injury", "hurt", "bleeding",
        // 中文危险关键词
        "危险", "事故", "刹车失灵", "刹车坏了", "刹车不工作", "刹车失效",
        "无法启动", "无法停止", "无法刹车", "不能刹车", "电池起火",
        "电池冒烟", "电池爆炸", "冒烟", "着火", "起火", "紧急",
        "失控", "剧烈震动", "剧烈摇晃", "滑倒", "摔倒了", "被车撞",
        "碰撞", "受伤", "流血", "危险品", "有毒", "漏液", "漏电"
    };

    // 中优先级关键词（功能性问题 - 需要尽快处理）
    private static final String[] MEDIUM_PRIORITY_KEYWORDS = {
        // 英文功能关键词
        "not working", "not functioning", "malfunction", "broken", "damaged",
        "won't lock", "cannot lock", "unlock problem", "unlocking issue",
        "battery low", "battery dead", "battery dying", "battery issue",
        "battery problem", "won't charge", "charging problem", "charge issue",
        "slow", "too slow", "speeding", "too fast", "unstable",
        "wobble", "wobbly", "loose", "noise", "noisy", "squeaking",
        "squeaky", "rattling", "rattle", "grinding", "clicking",
        "light broken", "light not working", "display broken", "screen broken",
        "gps not working", "gps broken", "location wrong", "wrong location",
        "horn not working", "bell broken", "handlebar loose", "seat broken",
        // 中文功能关键词
        "不工作", "坏了", "损坏", "故障", "失灵", "坏了", "坏掉了",
        "无法锁定", "锁定问题", "解锁问题", "锁不上", "解锁失败",
        "电池没电", "电池不足", "电池问题", "电量低", "充不进电",
        "充电问题", "充电慢", "充电失败", "充电器坏了",
        "太慢了", "速度慢", "速度异常", "太快了", "超速",
        "不稳", "摇晃", "晃动", "松动", "零件松动", "有响声",
        "异响", "吱吱响", "嗡嗡响", "噪音大", "车铃坏了",
        "车灯不亮", "车灯坏了", "显示屏坏了", "屏幕碎了",
        "GPS不工作", "GPS坏了", "定位错误", "定位不准",
        "喇叭不响", "把手松动", "座椅坏了", "踏板坏了"
    };

    // 低优先级关键词（轻微/外观问题 - 可以稍后处理）
    private static final String[] LOW_PRIORITY_KEYWORDS = {
        // 英文外观关键词
        "scratch", "scratched", "scratches", "scratch mark", "scratched surface",
        "cosmetic", "cosmetic damage", "dirt", "dirty", "filthy", "mud",
        "clean", "needs cleaning", "dirty", "dust", "dusty", "mark",
        "stain", "stained", "mark on", "minor", "small", "tiny", "little",
        "tiny scratch", "small dent", "looks", "look", "appearance",
        "paint", "paint chip", "paint scratch", "paint damaged", "dent",
        "dented", "denting", "faded", "fading", "discolored", "discoloration",
        "rust", "rusty", "corrosion", "peeling", "chip", "chipped",
        "scuff", "scuffed", "smudge", "fingerprints", "sticker residue",
        // 中文外观关键词
        "划痕", "刮痕", "刮花", "蹭了", "蹭伤", "表面划伤",
        "外观", "外观问题", "外表", "脏了", "很脏", "污渍",
        "泥土", "泥巴", "灰尘", "积灰", "需要清洁", "该洗了",
        "轻微", "轻微的", "很小", "微小", "一点点",
        "小划痕", "小凹陷", "小坑", "小问题",
        "漆面", "漆面刮花", "掉漆", "漆掉了", "补漆",
        "凹陷", "凹进去", "凹陷的", "瘪了",
        "褪色", "掉色", "变色", "颜色变了",
        "生锈", "锈迹", "腐蚀", "起皮", "脱皮",
        "蹭掉了", "蹭破皮", "污渍", "指纹印", "贴纸残留"
    };

    /**
     * 根据描述内容自动判断优先级
     * - 包含高危关键词 → HIGH（危险/安全问题）
     * - 包含中优先级关键词 → MEDIUM（功能性问题）
     * - 包含轻微问题关键词 → LOW（外观问题）
     * - 其他 → NORMAL（普通问题）
     */
    private String determinePriority(String description) {
        if (description == null || description.isEmpty()) {
            return "NORMAL";
        }
        String lowerDesc = description.toLowerCase();

        // 第一优先级：检查高危关键词（危险/安全问题）
        for (String keyword : HIGH_PRIORITY_KEYWORDS) {
            if (lowerDesc.contains(keyword.toLowerCase())) {
                log.info("High priority keyword detected: {}", keyword);
                return "HIGH";
            }
        }

        // 第二优先级：检查中优先级关键词（功能性问题）
        for (String keyword : MEDIUM_PRIORITY_KEYWORDS) {
            if (lowerDesc.contains(keyword.toLowerCase())) {
                log.info("Medium priority keyword detected: {}", keyword);
                return "MEDIUM";
            }
        }

        // 第三优先级：检查低优先级关键词（外观/轻微问题）
        for (String keyword : LOW_PRIORITY_KEYWORDS) {
            if (lowerDesc.contains(keyword.toLowerCase())) {
                log.info("Low priority keyword detected: {}", keyword);
                return "LOW";
            }
        }

        return "NORMAL";
    }

    /**
     * 创建问题报告
     * 根据描述内容自动设置优先级（ID13: Prioritise feedback）
     * - 危险/安全问题 → HIGH
     * - 轻微/外观问题 → LOW
     * - 其他 → NORMAL
     */
    @Override
    public IssueReport create(Long userId, IssueReportRequest request) {
        log.info("Creating issue report for userId: {}, scooterId: {}, description: {}",
                userId, request.getScooterId(), request.getDescription());

        // 根据描述自动确定优先级（ID13）
        String autoPriority = determinePriority(request.getDescription());
        log.info("Auto-determined priority: {} for description: {}", autoPriority, request.getDescription());

        IssueReport report = new IssueReport();
        report.setUserId(userId);
        report.setScooterId(request.getScooterId());
        report.setDescription(request.getDescription());
        report.setStatus("PENDING");
        report.setPriority(autoPriority);
        report.setCreatedAt(java.time.LocalDateTime.now());
        issueReportMapper.insert(report);

        log.info("Issue report inserted with priority {}, generated ID: {}", autoPriority, report.getId());

        // 重新查询获取完整对象
        if (report.getId() != null) {
            IssueReport result = issueReportMapper.findById(report.getId());
            log.info("Fetched complete issue report: {}", result);
            return result;
        }
        return report;
    }

    /**
     * 查询所有问题报告
     */
    @Override
    public List<IssueReport> findAll() {
        return issueReportMapper.findAll();
    }

    /**
     * 查询用户提交的问题报告
     */
    @Override
    public List<IssueReport> findByUserId(Long userId) {
        return issueReportMapper.findByUserId(userId);
    }

    /**
     * 根据ID查询问题报告
     */
    @Override
    public IssueReport findById(Long id) {
        return issueReportMapper.findById(id);
    }

    /**
     * 按优先级查询问题报告（ID14）
     */
    @Override
    public List<IssueReport> findByPriority(String priority) {
        return issueReportMapper.findByPriority(priority);
    }

    /**
     * 更新问题报告
     */
    @Override
    public boolean update(IssueReport issue) {
        return issueReportMapper.update(issue) > 0;
    }
}
