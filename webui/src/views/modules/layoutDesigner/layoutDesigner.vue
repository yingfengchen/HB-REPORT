<!-- 电路图编辑器页面 -->
<template>
  <div>
    <div id="components-layout">
      <a-row :style="{ height: height + 'px' }">
        <!-- {{tableDefaultData}} -->
        <a-col :span="24" style="display: flex; align-items: center; height: 6%">
          <a-card style="width: 100%" class="topTool">
            <a-select v-model="belong" default-value="HB_01" style="width: 120px; margin-right: 10px">
              <a-select-option value="HB_01">
                HB_01
              </a-select-option>
              <a-select-option value="HB_02">
                HB_02
              </a-select-option>
            </a-select>
            <a-button type="primary" style="margin-right: 10px" @click="handleImportLayout">
              导入Layout
            </a-button>
            <a-dropdown placement="bottomLeft">
              <template #overlay>
                <a-menu @click="testD">
                  <a-menu-item key="SVG">
                    导出 SVG
                  </a-menu-item>
                  <a-menu-item key="JSON">
                    导出 JSON
                  </a-menu-item>
                </a-menu>
              </template>
              <a-button type="primary" style="margin-right: 10px">
                导出数据
              </a-button>
            </a-dropdown>
            <a-button type="primary" @click="handleSaveLayout">
              发布
            </a-button>
            <label style="text-align: right; margin-left: 10px; color: #ff9191">未保存, 请发布后再离开页面</label>
          </a-card>
        </a-col>
        <a-col :span="24" class="pageMain">
          <a-row style="height: 100%" :gutter="6">
            <a-col :span="19" class="centerContain">
              <div class="canvas-content svg-main" id="canvas" @mousemove="MouseMove" @mousedown="MousedownCanvas"
                   @mouseup="MouseupCanvas" @dblclick="DblClick" @mousewheel="MouseWheel">
                <!--拖动辅助线-->
                <div id="guide-x"></div>
                <div id="guide-y"></div>
                <!-- 画布 -->
                <svg id="main_svg" version="1.1" xmlns="http://www.w3.org/2000/svg"
                     xmlns:xlink="http://www.w3.org/1999/xlink"
                     viewBox="0 0 1210 482"
                >
                  <defs/>
                  <filter x="0" y="0" width="1" height="1" id="solid">
                    <feFlood flood-color="rgb(255,255,255)"/>
                    <feComposite in="SourceGraphic"/>
                  </filter>
                  <g
                    style="cursor:pointer"
                    v-for="(item,index) in svgLists"
                    :key="svgLists.indexOf(item)"
                    :id=item.id
                    :title=item.title
                    :transform="'translate('+(item.svgPositionX)+','+(item.svgPositionY)+')' +'rotate('+item.angle+')'"
                    :class="item.class"
                    @mousedown="MousedownSvg(item.id,index)"
                  >
                    <SvgComponents
                      :height=item.height
                      :color=item.svgColor
                      :width=item.width
                      :type=item.type
                      :fontSize=item.fontSize
                      :svgText=item.svgText
                      :editable=editable
                      :stroke-color="item.strokeColor"
                      :class-name="item.class"
                    />
                  </g>
                </svg>
              </div>
            </a-col>
            <a-col :span="5" style="height: 100%">
              <a-row style="height: 100%">
                <a-col class="container-border" :span="24" style="height: 50%; overflow-y: auto">
                  <a-card :body-style="{ minHeight: (height/2 - 31) + 'px' }">
                    <LeftToolBar></LeftToolBar>
                  </a-card>
                </a-col>
                <a-col class="container-border" :span="24" style="margin-top: 5px; height: calc(50% - 5px); overflow-y: auto">
                  <a-card :body-style="{ minHeight: (height/2 - 36) + 'px' }">
                    <RightToolBar :svgInfo=selectSvgInfo @zIndexChange="handleZIndexChange" />
                  </a-card>
                </a-col>
              </a-row>
            </a-col>
          </a-row>
        </a-col>
      </a-row>
    </div>
  </div>
</template>
<script>
import LeftToolBar from './components/LeftToolBar.vue';
import RightToolBar from './components/RightToolBar.vue';
import SvgComponents from './components/SvgComponents.vue';
import global from './global/global.js';//全局变量
import UCore from './util/UCore'
import { saveLayoutInfo, saveLayoutFile, executeSQL } from '@api/api'

export default {
  components: {LeftToolBar, RightToolBar, SvgComponents},
  data() {
    return {
      shrink: true,//收缩状态  true收缩  false展开
      svgLists: [],
      //鼠标位置
      mousePosition: {
        positionX: '',
        positiony: ''
      },
      //辅助线元素
      guideX: '',
      guideY: '',
      CurrentlySelectedToolBarType: '',//选中的工具栏svg类型
      CurrentlySelectedToolBarTypeName: '',//选中的工具栏svg类型名称
      CurrentlySelectedToolBarTitle: '',//选中的工具栏svg标题
      CurrentlySelectedToolBarColor: '',//选中的工具栏svg颜色
      CurrentlySelectedToolBarHeight: '',//选中的工具栏svg高度
      CurrentlySelectedToolBarFontSize: '',//选中的工具栏svg字体大小
      CurrentlySelectedToolBarText: '',//选中的工具栏svg文字
      CurrentlySelectedToolBarWidth: '',//选中的工具栏svg高度
      CurrentlySelectedToolBarAngle: '',//选中的工具栏svg角度
      moveSvgID: '',//要移动的svg
      moveSvgIndex: 0,
      testvalue: 0,
      selectSvgInfo: '',
      tableRowCount: 2,//表格默认行数
      tableColCount: 2,//表格默认列数
      tableDefaultData: [],
      canvasZoom: 70,//画布缩放百分比
      userInfo: '',
      editable: true,
      belong: 'HB_01'
    }
  },
  computed: {
    height() {
      return this.$store.getters.bodyHeight - 130
    }
  },
  methods: {
    MouseMove(e) {
      if (e.offsetX == -1) {
        return;
      }
      if (e.target.nodeName == 'INPUT') {
        return;
      }
      if (e.offsetY == -1) {
        return;
      }
      if (this.moveSvgID == '') {
        return;
      }
      this.mousePosition.positionX = e.offsetX;
      this.mousePosition.positionY = e.offsetY;
      if (this.mousePosition.positionX < 1) {
        this.mousePosition.positionX = 10;
      }
      if (this.mousePosition.positionY < 1) {
        this.mousePosition.positionY = 10;
      }
      let svgID = this.svgLists[this.moveSvgIndex].id;
      //排除当前元素剩下的所有svg元素的列表
      let anyPositionList = this.svgLists.filter(function (list) {
        return list.id != svgID
      });
      //将要移动的元素坐标设备为鼠标坐标
      let svgPositionX = this.mousePosition.positionX;
      let svgPositionY = this.mousePosition.positionY;
      let _this = this;
      setTimeout(function () {
        //少于十个像素自动吸附
        //从所有的x坐标列表中查与当前坐标少于10个像素的组件是否存在
        let exitsAdsorbX = anyPositionList.filter(function (list) {
          return -10 < (list.svgPositionX - svgPositionX) && (list.svgPositionX - svgPositionX) < 10
        });
        if (exitsAdsorbX.length > 0) {
          svgPositionX = exitsAdsorbX[0].svgPositionX;
        }
        //y轴相同 横向坐标自动吸附
        let exitsAdsorbY = anyPositionList.filter(function (list) {
          return -10 < (list.svgPositionY - svgPositionY) && (list.svgPositionY - svgPositionY) < 10
        });
        if (exitsAdsorbY.length > 0) {
          svgPositionY = exitsAdsorbY[0].svgPositionY;
        }
        _this.svgLists[_this.moveSvgIndex].svgPositionX = svgPositionX;
        _this.svgLists[_this.moveSvgIndex].svgPositionY = svgPositionY;
        //从所有的x坐标列表中查当前坐标是否存在
        let exitsNowX = anyPositionList.filter(function (list) {
          return list.svgPositionX === svgPositionX
        });
        if (exitsNowX.length > 0) {
          _this.guideY.style.setProperty('left', svgPositionX - 1 + 'px');
          _this.guideY.style.display = 'inline';
        } else {
          _this.guideY.style.display = 'none';
        }
        //从所有的y坐标列表中查当前坐标是否存在
        let exitsNowY = anyPositionList.filter(function (list) {
          return list.svgPositionY === svgPositionY
        });
        if (exitsNowY.length > 0) {
          _this.guideX.style.setProperty('top', svgPositionY + 'px');
          _this.guideX.style.display = 'inline';
        } else {
          _this.guideX.style.display = 'none';
        }
      }, 1);
    },
    MousedownCanvas() {
      //console.log('点击了画布');
    },
    MousedownSvg(id, index) {
      global.CurrentlySelectedToolBarType = '';
      global.CurrentlySelectedToolBarTitle = '';
      this.CurrentlySelectedToolBarType = '';
      this.CurrentlySelectedToolBarTitle = '';
      //从数组里面根据index找到当前元素
      this.moveSvgID = id;
      this.moveSvgIndex = index;
      this.selectSvgInfo = this.svgLists[index];
      //获取所有g标签 将当前标签追加选中样式
      let gAnyList = document.querySelectorAll('g');
      gAnyList.forEach(g => {
        g.classList.remove("topo-layer-view-selected")
      });
      document.getElementById(id).classList.add("topo-layer-view-selected");
    },
    MouseupCanvas() {
      this.guideX.style.display = 'none';
      this.guideY.style.display = 'none';
      if (this.CurrentlySelectedToolBarTitle != '' || this.CurrentlySelectedToolBarType != '') {
        return;
      }
      this.moveSvgID = '';
    },
    MouseWheel(e) {
      //获取当前选中组件
      let selectSvgInfo = this.selectSvgInfo;
      if (selectSvgInfo == undefined || selectSvgInfo == null || selectSvgInfo == '') {
        return;
      }
      e.preventDefault();
      //判断滚轮方向 -100是往上滑 100是下滑
      let svgZoom = e.wheelDelta > 0 ? "5" : "-5";
      selectSvgInfo.height = parseInt(selectSvgInfo.height) + parseInt(svgZoom);
      if (selectSvgInfo.height < 1) {
        selectSvgInfo.height = 1;
      }
    },
    DblClick() {
      let svg_ele = document.getElementById('main_svg')
      console.log(svg_ele);
      //获取所有g标签 清除所有选中样式
      let gAnyList = document.querySelectorAll('g');
      gAnyList.forEach(g => {
        g.classList.remove("topo-layer-view-selected")
      });
      this.selectSvgInfo = '';
    },
    testD(data) {
      if(data.key === 'SVG') {
        let svg_ele = document.getElementById('main_svg')
        alert(svg_ele.outerHTML)
      }else if(data.key === 'JSON') {
        alert(JSON.stringify(this.svgLists));
      }
    },
    handleSaveLayout() {
      saveLayoutInfo({layout_name: this.belong, items: this.svgLists}).then(r => {
        let svg_ele = document.getElementById('main_svg')
        saveLayoutFile({layout_str: svg_ele.outerHTML, layout_name: this.belong}).then(r => {
          if(r['success'] && r['result'] !== 'Error') {
            this.$notification.success({
              message: '信息',
              description: '保存成功'
            })
          }
        })
      })
    },
    handleImportLayout() {
      executeSQL({sql_name: 'getLayoutByName', belong: this.belong}).then(r => {
        if(r['success']){
          this.svgLists = r['result']
        }else{
          this.$notification['error']({
            message: '错误',
            description: r['message']
          });
        }
      })
    },
    handleZIndexChange(value) {
      const temp = JSON.parse(JSON.stringify(this.svgLists))
      this.svgLists = []
      temp.sort((a, b) => { return a['zIndex'] - b['zIndex'] })
      this.svgLists = temp
    }
  },
  mounted() {
    let _this = this;
    let canvasdiv = document.querySelector('#canvas');
    _this.guideX = document.querySelector('#guide-x');//辅助线x轴
    _this.guideY = document.querySelector('#guide-y');//辅助线y轴
    canvasdiv.addEventListener("dragover", function (e) {
      e.preventDefault();
      _this.CurrentlySelectedToolBarType = global.CurrentlySelectedToolBarType;
      _this.CurrentlySelectedToolBarTitle = global.CurrentlySelectedToolBarTitle;
      _this.CurrentlySelectedToolBarTypeName = global.CurrentlySelectedToolBarTypeName;
      _this.CurrentlySelectedToolBarColor = global.CurrentlySelectedToolBarColor;
      _this.CurrentlySelectedToolBarHeight = global.CurrentlySelectedToolBarHeight;
      _this.CurrentlySelectedToolBarFontSize = global.CurrentlySelectedToolBarFontSize;
      _this.CurrentlySelectedToolBarText = global.CurrentlySelectedToolBarText || '';
      _this.CurrentlySelectedToolBarWidth = global.CurrentlySelectedToolBarWidth;
      _this.CurrentlySelectedToolBarAngle = global.CurrentlySelectedToolBarAngle;
    }, false);
    canvasdiv.addEventListener("drop", function (e) {
      e.preventDefault();
      if (_this.CurrentlySelectedToolBarType == '') {
        return;
      }
      //根据类型和鼠标位置创建组件
      let svgItem = {
        id: UCore.GenUUid(),
        sort: 0,
        title: _this.CurrentlySelectedToolBarTitle,
        type: _this.CurrentlySelectedToolBarType,
        class: '',
        typeName: _this.CurrentlySelectedToolBarTypeName,
        svgColor: _this.CurrentlySelectedToolBarColor,
        svgPositionX: e.offsetX,
        svgPositionY: e.offsetY,
        height: _this.CurrentlySelectedToolBarHeight,
        width: _this.CurrentlySelectedToolBarWidth,
        strokeColor: '#000000',
        fontSize: _this.CurrentlySelectedToolBarFontSize,
        svgText: _this.CurrentlySelectedToolBarText,
        tableRowCount: _this.tableRowCount,
        tableColCount: _this.tableColCount,
        angle: _this.CurrentlySelectedToolBarAngle,
        zIndex: 1
      };
      _this.svgLists.push(svgItem);
      setTimeout(function () {
        //获取所有g标签 将当前标签追加选中样式
        let gAnyList = document.querySelectorAll('g');
        gAnyList.forEach(g => {
          g.classList.remove("topo-layer-view-selected")
        });
        document.getElementById(svgItem.id).classList.add("topo-layer-view-selected");
        _this.selectSvgInfo = svgItem;
      }, 100);
    }, false);

    this.handleImportLayout()
  },
  created() {
    let _this = this;
    //当前页面监视键盘输入
    document.onkeydown = function (e) {
      //获取当前选中组件
      let selectSvgInfo = _this.selectSvgInfo;
      if (selectSvgInfo == undefined || selectSvgInfo == null || selectSvgInfo == '') {
        return;
      }
      //事件对象兼容
      let e1 = e || window.event || arguments.callee.caller.arguments[0]
      //键盘按键判断:左箭头-37;上箭头-38；右箭头-39;下箭头-40
      if (e1 && e1.keyCode == 37) {
        e.preventDefault();
        selectSvgInfo.svgPositionX -= 1;
      } else if (e1 && e1.keyCode == 38) {
        e.preventDefault();
        selectSvgInfo.svgPositionY -= 1;
      } else if (e1 && e1.keyCode == 39) {
        e.preventDefault();
        selectSvgInfo.svgPositionX += 1;
      } else if (e1 && e1.keyCode == 40) {
        e.preventDefault();
        selectSvgInfo.svgPositionY += 1;
      }
      //ctrl  c
      else if (e1 && e1.ctrlKey && e1.keyCode == 67) {
        e.preventDefault();
        let copySvgInfoStr = JSON.stringify(selectSvgInfo);
        let copySvgInfo = JSON.parse(copySvgInfoStr);
        copySvgInfo.id = UCore.GenUUid();
        copySvgInfo.svgPositionX = selectSvgInfo.svgPositionX + 10;
        copySvgInfo.svgPositionY = selectSvgInfo.svgPositionY + 10;
        _this.svgLists.push(copySvgInfo);
        _this.selectSvgInfo = copySvgInfo;
        setTimeout(function () {
          //获取所有g标签 将当前标签追加选中样式
          let gAnyList = document.querySelectorAll('g');
          gAnyList.forEach(g => {
            g.classList.remove("topo-layer-view-selected")
          });
          document.getElementById(copySvgInfo.id).classList.add("topo-layer-view-selected");
        }, 100);
      }
      //deleted
      else if (e1 && e1.keyCode == 46) {
        e.preventDefault();
        //根据当前id找到当前元素的index
        let selectSvgIndex = _this.svgLists.indexOf(_this.svgLists.filter(f => f.id == selectSvgInfo.id)[0]);
        _this.svgLists.splice(selectSvgIndex, 1);
      }
    }
  }
}
</script>

<style scoped lang="less">
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  border-radius: 10px;
  background-color: #f2f2f2;
  box-shadow: 1px 1px 5px #838383 inset;
}

::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background-color: #a2a2a2;
}

.topTool {
  ::v-deep .ant-card-body {
    padding: 15px;
  }
}

.svg-main {
  border-radius: 3px;
  border: 1px solid #a9a9a9;
  background: #fcfcfc;
  background-image:
    linear-gradient(rgba(171, 171, 171, 0.3) 1px, transparent 0),
    linear-gradient(90deg, rgba(183, 183, 183, 0.43) 1px, transparent 0),
    linear-gradient(#9d9d9d 1px, transparent 0),
    linear-gradient(90deg, #c3c3c3 1px, transparent 0);
  background-size: 15px 15px, 15px 15px, 75px 75px, 75px 75px;
}

.pageMain {
  height: 94%;
  left: 0;
  right: 0;
  top: 12px;
  bottom: 0;
  overflow-y: auto;
  overflow-x: hidden;

  ::v-deep .ant-card-body {
    padding: 10px;
  }
}

.centerContain {
  height: 100%;
  overflow: auto !important;
  transition: all 0.3s;
  z-index: 111;
  display: flex;
  justify-content: center;
  align-items: center;

  &.fixed {
    position: fixed;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
  }


  .canvas-content {
    width: 1210px;
    height: 482px;
  }

  .btns-content {
    position: fixed;
    bottom: 10px;
    right: 320px;
    left: 280px;

    button {
      margin-left: 10px;
    }
  }
}

#components-layout .ant-layout-sider {
  background: #fff;
  color: rgb(167, 164, 164);
}

#guide-x {
  display: none;
  position: absolute;
  border-top: 1px dashed #55f;
  width: 100%;
  left: 0px;
  top: 500px;

}

#guide-y {
  display: none;
  position: absolute;
  border-left: 1px dashed #55f;
  height: 100%;
  left: 100px;
  top: 0px;


}

#canvas {
  -moz-user-select: none;
  -webkit-user-select: none;
  -ms-user-select: none;
  -khtml-user-select: none;
  user-select: none;
}

.topo-layer {
  width: 100%;
  height: 100%;
  position: absolute;
  transform-origin: left top;
  overflow: auto;


  background-image: linear-gradient(45deg, #ccc 25%, transparent 25%, transparent 75%, #ccc 75%, #ccc),
  linear-gradient(45deg, #ccc 25%, transparent 25%, transparent 75%, #ccc 75%, #ccc);
  background-size: 20px 20px;
  background-position: 0 0, 10px 10px;
}

.topo-layer-view-selected {
  outline: 1px solid #0cf;
}

.container-border {
  border-radius: 5px;
  border: 1px solid #b8b8b8;
}
</style>
