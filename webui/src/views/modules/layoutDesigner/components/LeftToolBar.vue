<template>
  <div class="components-layout-left">

    <a-input-search placeholder="搜索组件" @search="onSearch"/>
    <a-collapse v-model:activeKey="activeKey">
      <a-collapse-panel key="1" header="设备">
        <ul class="svg-nav-list">
          <li v-for="item in machineListT" :key="machineListT.indexOf(item)">
            <div class="title">{{ item.title }}</div>
            <img :title="item.title"
                 @mousedown="Mousedown( item.type, item.title, item.color, item.height, item.fontSize, item.text, item.width)"
                 :src="require('../assets/'+item.imageSrc+'.svg')" draggable="true">
          </li>
        </ul>
      </a-collapse-panel>
      <a-collapse-panel key="2" header="辅助元素">
        <ul class="svg-nav-list">
          <li v-for="item in assistItemListT" :key="assistItemListT.indexOf(item)">
            <div class="title">{{ item.title }}</div>
            <img :title="item.title"
                 @mousedown="Mousedown( item.type, item.title, item.color, item.height, item.fontSize, item.text, item.width)"
                 :src="require('../assets/'+item.imageSrc+'.svg')"
                 draggable="true">
          </li>
        </ul>
      </a-collapse-panel>
    </a-collapse>
  </div>
</template>
<script>
import global from '../global/global.js';//全局变量
export default {
  data() {
    return {
      activeKey: ['1'],//当前激活的key
      text: `这里是预留位置.`,
      machineListT: [
        {
          title: '线体',
          type: 'InlineSvg',
          color: '#DDDDDD',
          height: 200,
          fontSize: 0,
          width: 2,
          imageSrc: 'inline',
          text: ''
        },
        {
          title: '单元',
          type: 'UnitSvg',
          color: '#DDDDDD',
          height: 200,
          fontSize: 0,
          width: 2,
          imageSrc: 'unit',
          text: ''
        },
        {
          title: '子单元',
          type: 'SubUnitSvg',
          color: '#DDDDDD',
          height: 200,
          fontSize: 0,
          width: 2,
          imageSrc: 'subUnit',
          text: ''
        },
        {
          title: 'Port',
          type: 'PortSvg',
          color: '#DDDDDD',
          height: 40,
          fontSize: 0,
          width: 2,
          imageSrc: 'port',
          text: ''
        }
      ],
      assistItemListT: [
        {
          title: 'EQSlot',
          type: 'EQSlotSvg',
          color: '#DDDDDD',
          height: 200,
          fontSize: 0,
          width: 2,
          imageSrc: 'eqslot',
          text: ''
        },
        {
          title: 'Conveyor',
          type: 'ConveyorSvg',
          color: '#ccc1da',
          height: 200,
          fontSize: 0,
          width: 3,
          imageSrc: 'conveyor',
          text: ''
        },
        {
          title: 'Stage',
          type: 'StageSvg',
          color: '#4bacc6',
          height: 200,
          fontSize: 0,
          width: 2,
          imageSrc: 'stage',
          text: ''
        },
        {
          title: 'NGStage',
          type: 'NGStageSvg',
          color: '#ff0000',
          height: 200,
          fontSize: 0,
          width: 2,
          imageSrc: 'ngStage',
          text: ''
        },
        {
          title: 'VCR',
          type: 'VCRSvg',
          color: '#ff0000',
          height: 200,
          fontSize: 0,
          width: 2,
          imageSrc: 'vcr',
          text: ''
        },
        {
          title: 'BCR',
          type: 'BCRSvg',
          color: '#ffff00',
          height: 200,
          fontSize: 0,
          width: 2,
          imageSrc: 'bcr',
          text: ''
        },
        {
          title: '箭头',
          type: 'ArrowSvg',
          color: '#b3ffb4',
          height: 20,
          fontSize: 0,
          width: 2,
          imageSrc: 'arrow',
          text: ''
        }
      ]
    };
  },
  methods: {
    Mousedown(type, title, color, height, fontSize, text, width) {
      global.CurrentlySelectedToolBarType = type;
      global.CurrentlySelectedToolBarTypeName = title;
      global.CurrentlySelectedToolBarTitle = title;
      global.CurrentlySelectedToolBarColor = color;
      global.CurrentlySelectedToolBarHeight = height;
      global.CurrentlySelectedToolBarFontSize = fontSize;
      global.CurrentlySelectedToolBarText = text;
      global.CurrentlySelectedToolBarWidth = width;
      // alert(this.$UCore.GenUUid());
    },
    onSearch(data) {
      this.machineListT = this.machineList.filter((m) => {return m.title.indexOf(data) > -1})
      this.assistItemListT = this.assistItemList.filter((a) => {return a.title.indexOf(data) > -1})
    }
  }
};
</script>
<style>
.components-layout-left .ant-input-search-icon {
  font-size: 20px !important;
  color: #1890ff !important;
}

.components-layout-left .ant-collapse,
.components-layout-left .ant-collapse-content,
.components-layout-left .ant-collapse > .ant-collapse-item {
  border-color: #eee !important;
}

.components-layout-left .ant-collapse-content > .ant-collapse-content-box {
  padding: 16px 0;
}
</style>
<style lang="less" scoped>
.ant-input-affix-wrapper {
  height: 45px;
  line-height: 45px;
  border: none;
}

.svg-nav-list {
  display: flex;
  flex-wrap: wrap;
  list-style: none;
  margin: 0;
  padding: 0;

  li {
    position: relative;
    width: calc(33% - 30px);
    margin: 0 15px 15px 15px;
    padding: 0;
    border-radius: 5%;
    box-shadow: 1px 1px 5px #ddd;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;

    &:hover {
      box-shadow: 1px 1px 5px #9d9d9d;
    }

    img {
      display: block;
      width: 100%;
    }

    .title {
      display: none;
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: rgba(0, 0, 0, 0.4);
      color: #fff;
      height: 20px;
      line-height: 20px;
      font-size: 12px;
      text-align: center;
    }
  }


  &.two-item {
    li {
      width: calc(50% - 30px);
      height: 100px;
      margin-bottom: 25px;
      border-radius: 10px;

      img {
        width: auto;
        height: 100%;
      }
    }
  }
}
</style>
