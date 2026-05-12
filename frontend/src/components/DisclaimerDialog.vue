<template>
  <el-dialog
    v-model="visible"
    title="用户协议与免责声明"
    width="680px"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <div class="disclaimer-content">
      <div class="section">
        <h4>一、服务条款</h4>
        <p>使用 CapyGlide 电动滑板车租赁服务前，请仔细阅读以下条款。使用本服务即表示您同意遵守以下所有条款和条件。</p>
      </div>

      <div class="section">
        <h4>二、年龄与资格要求</h4>
        <ul>
          <li>用户必须年满 <strong>16 周岁</strong> 方可使用本服务。</li>
          <li>16-18 周岁的用户需在监护人同意下方可使用。</li>
          <li>用户需具备完全民事行为能力，或在监护人陪同下使用。</li>
        </ul>
      </div>

      <div class="section">
        <h4>三、安全责任</h4>
        <ul>
          <li><strong>骑行安全：</strong>用户应佩戴安全头盔，遵守交通规则，确保自身及他人安全。</li>
          <li><strong>骑行限制：</strong>禁止在机动车道行驶，禁止搭载他人，禁止酒驾。</li>
          <li><strong>损坏责任：</strong>因用户原因造成的车辆损坏，需承担相应赔偿责任。</li>
          <li><strong>盗窃风险：</strong>用户需妥善保管车辆，如因保管不当导致丢失，需承担赔偿责任。</li>
        </ul>
      </div>

      <div class="section">
        <h4>四、交通保险说明</h4>
        <p>根据英国/中国相关法律规定，本服务包含基础交通意外保险：</p>
        <ul>
          <li><strong>保险范围：</strong>用户在正常使用过程中发生的意外伤害。</li>
          <li><strong>赔偿限额：</strong>意外伤害身故/残疾最高赔偿限额为 ¥100,000。</li>
          <li><strong>除外责任：</strong>以下情况不在保险赔付范围内：
            <ul class="sub-list">
              <li>用户故意行为导致的伤害</li>
              <li>酒驾、毒驾等违法行为导致的伤害</li>
              <li>违反交通规则导致的伤害</li>
              <li>战争、暴乱等不可抗力导致的伤害</li>
            </ul>
          </li>
        </ul>
      </div>

      <div class="section">
        <h4>五、计费与支付</h4>
        <ul>
          <li>租金按租用时长计算，超时将自动扣除额外费用。</li>
          <li>电费按行驶里程计算（每公里 ¥0.50）。</li>
          <li>超时费用：每分钟 ¥0.50。</li>
          <li>用户需保证账户余额充足，如余额不足导致无法完成支付，可能产生滞纳金。</li>
        </ul>
      </div>

      <div class="section">
        <h4>六、车辆损坏处理</h4>
        <ul>
          <li>还车时请检查车辆外观，如有损坏请及时拍照留证并报告。</li>
          <li>如车辆在租用期间被损坏，用户需承担维修费用或按原价赔偿。</li>
          <li>对于恶意损坏车辆的行为，我们将依法追究法律责任。</li>
        </ul>
      </div>

      <div class="section">
        <h4>七、免责声明</h4>
        <p><strong>在法律允许的最大范围内，CapyGlide 对以下情况不承担责任：</strong></p>
        <ul>
          <li>因用户违反交通规则或操作不当导致的任何损失或伤害。</li>
          <li>因不可抗力（如恶劣天气、战争、暴乱等）导致的服务中断。</li>
          <li>因用户自身健康原因（如心脏病、高血压等）在骑行过程中发生的问题。</li>
          <li>因第三方行为导致的用户损失。</li>
        </ul>
      </div>

      <div class="section">
        <h4>八、隐私保护</h4>
        <p>我们承诺保护您的个人隐私，您的位置信息和行程数据仅用于提供服务，不会泄露给第三方（法律要求除外）。</p>
      </div>

      <div class="section">
        <h4>九、服务变更</h4>
        <p>CapyGlide 保留随时修改服务条款、费用标准的权利。修改后的条款将在网站/APP上公布，修改内容自公布之日起生效。</p>
      </div>

      <div class="section">
        <h4>十、适用法律</h4>
        <p>本服务条款受英国/中华人民共和国法律管辖。如产生争议，双方应友好协商解决；如协商不成，可向当地法院提起诉讼。</p>
      </div>

      <div class="agree-section">
        <el-checkbox v-model="agreed" size="large">
          我已阅读并充分理解上述条款，同意接受所有条款约束
        </el-checkbox>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button size="large" @click="handleDecline">不同意</el-button>
        <el-button type="primary" size="large" :disabled="!agreed" @click="handleAccept">
          同意并继续
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'accept', 'decline'])

const visible = ref(props.modelValue)
const agreed = ref(false)

watch(() => props.modelValue, (val) => {
  visible.value = val
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const handleAccept = () => {
  if (agreed.value) {
    emit('accept')
    visible.value = false
    agreed.value = false
  }
}

const handleDecline = () => {
  emit('decline')
  visible.value = false
  agreed.value = false
}
</script>

<style scoped>
.disclaimer-content {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 8px;
}

.disclaimer-content::-webkit-scrollbar {
  width: 6px;
}

.disclaimer-content::-webkit-scrollbar-thumb {
  background: #dcdfe6;
  border-radius: 3px;
}

.section {
  margin-bottom: 20px;
}

.section h4 {
  margin: 0 0 8px;
  color: var(--cg-navy);
  font-size: 15px;
  font-weight: 700;
}

.section p {
  margin: 0;
  color: var(--cg-text);
  line-height: 1.7;
  font-size: 14px;
}

.section ul {
  margin: 8px 0;
  padding-left: 20px;
}

.section li {
  color: var(--cg-text);
  line-height: 1.8;
  font-size: 14px;
}

.section li strong {
  color: var(--cg-text);
}

.sub-list {
  margin-top: 6px;
}

.sub-list li {
  color: var(--cg-text-light);
  font-size: 13px;
}

.agree-section {
  margin-top: 24px;
  padding: 16px;
  background: var(--cg-bg);
  border-radius: var(--cg-radius-md);
  border: 1px solid var(--cg-border);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
