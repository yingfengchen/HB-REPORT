<!-- 箭头向下svg组件 -->
<template>
  <Inline v-if="type==='InlineSvg'" :height="height" :color="color" :width="width" :eq-id="eqId" :class="className" />
  <Unit v-else-if="type==='UnitSvg'" :height="height" :color="color" :width="width" :text="svgText" :class="className" :stroke-color="strokeColor" />
  <SubUnit v-else-if="type==='SubUnitSvg'" :height="height" :color="color" :width="width" :class="className" />
  <port v-else-if="type==='PortSvg'" :height="height" :color="color" :width="width" :class="className" :text="svgText" :stroke-color="strokeColor" />
  <EQSlot v-else-if="type==='EQSlotSvg'" :height="height" :color="color" :width="width" :class="className" :text="svgText" />
  <Conveyor v-else-if="type==='ConveyorSvg'" :height="height" :color="color" :width="width" :class="className" />
  <Stage v-else-if="type==='StageSvg'" :height="height" :color="color" :width="width" :class="className" />
  <NGStage v-else-if="type==='NGStageSvg'" :height="height" :color="color" :width="width" :class="className" />
  <VCR v-else-if="type==='VCRSvg'" :height="height" :color="color" :width="width" :class="className" />
  <BCR v-else-if="type==='BCRSvg'" :height="height" :color="color" :width="width" :class="className" />
  <Arrow v-else-if="type==='ArrowSvg'" :height="height" :color="color" :width="width" :class="className" />

</template>
<script>
import Inline from './svgCpt/Inline'
import Unit from './svgCpt/Unit'
import SubUnit from './svgCpt/SubUnit'
import Port from './svgCpt/Port'
import EQSlot from './svgCpt/EQSlot'
import Conveyor from './svgCpt/Conveyor'
import Stage from './svgCpt/Stage'
import NGStage from './svgCpt/NGStage'
import VCR from './svgCpt/VCR'
import BCR from './svgCpt/BCR'
import Arrow from './svgCpt/Arrow'

export default {
  components: {
    Inline,
    Unit,
    Port,
    SubUnit,
    EQSlot,
    Conveyor,
    Stage,
    NGStage,
    VCR,
    BCR,
    Arrow
  },
  // eslint-disable-next-line vue/require-prop-types
  props: ['height', 'color', 'width', 'eqId', 'type', 'fontSize', 'tableData', 'svgText', 'editable', 'className', 'strokeColor']
}
</script>
