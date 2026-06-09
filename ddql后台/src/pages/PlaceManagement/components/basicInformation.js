import React from 'react';
import { PlusOutlined } from '@ant-design/icons';
import {
  Form,
  Input,
  Button,
  Row,
  Col,
  Spin,
  message,
  Popconfirm,
  Table,
  Select,
  DatePicker,
  Tabs,
  Checkbox,
  Upload,
  Modal,
  Radio,
  Switch,
  TimePicker,
  Alert,
  Cascader,
} from 'antd';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { history, connect, Link } from 'umi';
import ImgCrop from 'antd-img-crop';
import { Map, Marker } from 'react-amap';
import CKEditor from 'react-ckeditor-wrapper';
import { HTML5Backend } from 'react-dnd-html5-backend';
import update from 'immutability-helper';
import moment from 'moment';
import dayjs from 'dayjs';
import { urlName } from '@/utils/utils';
// import { thisExpression } from '@babel/types';
// import { setToken } from '@/utils/authority';

const { Option } = Select;
const { RangePicker } = DatePicker;
// 场所基础信息
let dragingIndex = -1;
class BodyRow extends React.Component {
  state = {
    updataCloneList: [],
  };

  render() {
    const { isOver, connectDragSource, connectDropTarget, moveRow, ...restProps } = this.props;
    const style = { ...restProps.style, cursor: 'move' };

    let { className } = restProps;
    if (isOver) {
      if (restProps.index > dragingIndex) {
        className += ' drop-over-downward';
      }
      if (restProps.index < dragingIndex) {
        className += ' drop-over-upward';
      }
    }

    return connectDragSource(
      connectDropTarget(<tr {...restProps} className={className} style={style} />),
    );
  }
}

const rowSource = {
  beginDrag(props) {
    dragingIndex = props.index;
    return {
      index: props.index,
    };
  },
};

const rowTarget = {
  drop(props, monitor) {
    const dragIndex = monitor.getItem().index;
    const hoverIndex = props.index;

    // Don't replace items with themselves
    if (dragIndex === hoverIndex) {
      return;
    }

    // Time to actually perform the action
    props.moveRow(dragIndex, hoverIndex);

    // Note: we're mutating the monitor item here!
    // Generally it's better to avoid mutations,
    // but it's good here for the sake of performance
    // to avoid expensive index searches.
    monitor.getItem().index = hoverIndex;
  },
};

const DragableBodyRow = DropTarget('row', rowTarget, (connect, monitor) => ({
  connectDropTarget: connect.dropTarget(),
  isOver: monitor.isOver(),
}))(
  DragSource('row', rowSource, (connect) => ({
    connectDragSource: connect.dragSource(),
  }))(BodyRow),
);

class Login extends React.Component {
  formRef = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    list: [],
    thumbnailFileList: [],
    carouselFileList: [],
    gymList: [],
    siteList: [],
    isOpen: false,
    locations: {},
    longitude: 113.880469,
    latitude: 22.889404,
    deskGetNumber: true,
    deskDisplay: true,
    serviceLists: [],
    rechargeOrderCountData: {},
    teamType: -1,
  };

  components = {
    body: {
      row: DragableBodyRow,
    },
  };

  componentDidMount() {
    const { edit, id, info } = this.props;
    
    const loadDizhi = () => {
      if (window.dizhi) {
        this.formatDizhi(window.dizhi, info, edit);
      } else {
        const c = document.createElement('script');
        c.src = 'https://admin.nctyt.com/dizhi.js';
        c.onload = () => {
          this.formatDizhi(window.dizhi, info, edit);
        };
        document.body.appendChild(c);
      }
    };
    loadDizhi();

    if (info && info.id) {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          searchId: info.id,
        },
        url: '/ddql/team/recharge/order/count',
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            this.setState({
              teamHealthyCoin: res.data.teamHealthyCoin,
              rechargeOrderCountData: res.data.rechargeOrderCountData,
            });
          } else {
            message.error(res.msg);
          }
        },
      });
    }
  }

  formatDizhi = (dizhiData, info, edit) => {
    const bbb = [];
    dizhiData.forEach((res) => {
      if (!bbb.some((resd) => resd.label === res.province)) {
        bbb.push({ label: res.province, value: res.province, children: [] });
      }
    });
    bbb.forEach((resd) => {
      const aa = dizhiData.filter((res) => res.province === resd.label);
      aa.forEach((res) => {
        if (!resd.children.some((child) => child.label === res.market)) {
          resd.children.push({
            label: res.market,
            value: res.market,
            children: []
          });
        }
      });
    });
    bbb.forEach((resd) => {
      resd.children.forEach((child) => {
        const aa = dizhiData.filter((res) => res.market === child.label);
        aa.forEach((res) => {
           if (!child.children.some((c) => c.label === res.distinguish)) {
              child.children.push({
                label: res.distinguish,
                value: res.distinguish,
              });
           }
        });
      });
    });
    this.setState({ regionOptions: bbb }, () => {
      if (edit == 1) {
        this.formRef.current.setFieldsValue({
          name: info.name,
          region: this.parseRegionStr(info.region, bbb),
          address: info.address,
          contactPerson: info.contactPerson,
          contactPhone: info.contactPhone,
          contactEmail: info.contactEmail,
          type: info.type,
          isMultiDepartment: info.isMultiDepartment,
        });
        this.setState({
          teamType: info.type,
        });
      }
    });
  };

  parseRegionStr = (str, regionOptions) => {
    if (!str) return [];
    let matchP, matchC, matchD;
    for (let p of regionOptions) {
      if (str.startsWith(p.value)) {
        matchP = p;
        let rest = str.substring(p.value.length);
        for (let c of p.children) {
          if (rest.startsWith(c.value)) {
            matchC = c;
            let rest2 = rest.substring(c.value.length);
            for (let d of c.children) {
               if (rest2.startsWith(d.value)) {
                 matchD = d;
                 break;
               }
            }
            break;
          }
        }
        break;
      }
    }
    const arr = [];
    if (matchP) arr.push(matchP.value);
    if (matchC) arr.push(matchC.value);
    if (matchD) arr.push(matchD.value);
    return arr.length > 0 ? arr : [str];
  };

  updateContent = (value, index) => {
    this.setState({
      content: value,
    });
  };
  showModal = () => {
    this.setState(
      {
        map: true,
      },
      () => {
        this.toolEvents = {
          created: (tool) => {
            this.tool = tool;
          },
        };
        // this.mapPlugins = ['ToolBar'];
        this.mapCenter = { longitude: 120, latitude: 35 };
        this.markerPosition = { longitude: 121, latitude: 36 };
      },
    );
  };
  handleCancelz = () => {
    this.setState({
      map: false,
    });
  };
  handleOkz = () => {
    this.setState(
      {
        map: false,
      },
      () => {
        this.setState({ locationValue: this.state.locations.name });
      },
    );
  };
  handleCancel = () => {
    this.setState({
      addGroupModalVisible: false,
    });
  };
  getAddress = () => {
    setTimeout((_) => {
      window.AMap.plugin('AMap.PlaceSearch', () => {
        var placeSearch = new window.AMap.PlaceSearch({
          pageSize: 10,
          pageIndex: 1,
          // citylimit: true, // 仅搜索本城市的地名
        });
        placeSearch.search(document.getElementById('tipinput').value, (res, result) => {
          if (result.poiList.pois.length == 0) {
            message.info('未搜索到相关地址，请重新输入');
            return;
          }
          this.setState(
            {
              AddressDetails:
                result.poiList &&
                result.poiList.pois &&
                result.poiList.pois[0] &&
                result.poiList.pois[0].location,
              latitude:
                result.poiList &&
                result.poiList.pois &&
                result.poiList.pois[0] &&
                result.poiList.pois[0].location.lat,
              longitude:
                result.poiList &&
                result.poiList.pois &&
                result.poiList.pois[0] &&
                result.poiList.pois[0].location.lng,
              locations: result.poiList && result.poiList.pois && result.poiList.pois[0],
            },

            () => { },
          );
        });
      });
    }, 150);
  };

  selectAddress = {
    // created必须要拥有,用来初始化创建相应对象
    created: () => {
      let auto;
      let placeSearch;
      window.AMap.plugin('AMap.Autocomplete', () => {
        auto = new window.AMap.Autocomplete({
          input: 'tipinput',
          pageSize: 10,
          pageIndex: 1,
          // citylimit: true, // 仅搜索本城市的地名
          // city: '昆明', // 限制为只能搜索当前地区的位置
          outPutDirAuto: true,
          // type: '汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施'
        });
      });
      // 创建搜索实例
      window.AMap.plugin('AMap.PlaceSearch', () => {
        placeSearch = new window.AMap.PlaceSearch({
          input: 'tipinput',
          pageSize: 10,
          pageIndex: 1,
          // citylimit: true, // 仅搜索本城市的地名
        });
      });

      window.AMap.event.addListener(auto, 'select', (e) => {
        placeSearch.search(e.poi.name);
      });
    },
  };

  getQrcode = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        id: this.props.info.id,
      },
      url: `/api/admin/stadium/quick/code`,
      method: 'GET',
      myData: (res) => {
        if (res && res.code === 200) {
          window.open(
            window.location.hostname == 'admin.sshtyt.cn'
              ? 'https://api.sshtyt.cn' + res.data
              : 'http://39.108.167.61:8006' + res.data,
            '_blank',
          );
          // let xhr = new XMLHttpRequest()
          // let fileName = `` // 文件名称
          // xhr.open('GET',res.data, true)
          // xhr.responseType = 'blob'
          // xhr.onload = function () {
          //     if (this.status === 200) {
          //         let type = xhr.getResponseHeader('Content-Type')

          //         let blob = new Blob([this.response], { type: type })
          //         if (typeof window.navigator.msSaveBlob !== 'undefined') {
          //             window.navigator.msSaveBlob(blob, fileName)
          //         } else {
          //             let URL = window.URL || window.webkitURL
          //             let objectUrl = URL.createObjectURL(blob)
          //             if (fileName) {
          //                 var a = document.createElement('a')
          //                 // safari doesn't support this yet
          //                 if (typeof a.download === 'undefined') {
          //                     window.location = objectUrl
          //                 } else {
          //                     a.href = objectUrl
          //                     a.download = fileName
          //                     document.body.appendChild(a)
          //                     a.click()
          //                     a.remove()
          //                 }
          //             } else {
          //                 window.location = objectUrl
          //             }
          //         }
          //     }
          // }
          // xhr.send()
        } else {
          message.error(res.message);
        }
      },
    });
  };

  onFinish = (values) => {
    const { info, edit, xxxx } = this.props;
    const params = {
      name: values.name,
      region: values.region ? (Array.isArray(values.region) ? values.region.join('') : values.region) : undefined,
      address: values.address,
      contactPerson: values.contactPerson,
      contactPhone: values.contactPhone,
      contactEmail: values.contactEmail,
      type: values.type,
      status: this.props.status,
      isMultiDepartment: values.isMultiDepartment,
      isUserAuth: 0,
    };
    if (edit == 1) {
      params.id = this.props.info.id;
    }
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        ...params,
      },
      url: edit == 1 ? '/ddql/team/update' : '/ddql/team/add',
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          if (this.props.onInfoUpdate) {
            this.props.onInfoUpdate({ ...info, ...params });
          }
          if (edit != 1) {
            setTimeout((_) => {
              history.goBack();
            }, 200);
          }
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  // 通用的文件变化处理
  handleUploadChange =
    (type) =>
      ({ file, fileList }) => {
        this.setState({ [type]: fileList }, () => {
          const { response = {} } = file;
          if (response.code == 10000) {
            const data = this.state[type];
            data[data.length - 1].response.data.url =
              urlName + data[data.length - 1].response.data.url;
            this.setState({
              [type]: data,
            });
          }
        });
      };

  // 通用的预览处理
  handlePreview = async (file) => {
    let src = file.url;
    if (!src) {
      src = await new Promise((resolve) => {
        const reader = new FileReader();
        reader.readAsDataURL(file.originFileObj);
        reader.onload = () => resolve(reader.result);
      });
    }
    this.setState({
      previewImage: src,
      previewOpen: true,
    });
  };

  // 关闭预览
  handleCancelPreview = () => {
    this.setState({ previewOpen: false });
  };

  addGroup = () => {
    const data = JSON.parse(JSON.stringify(this.state.serviceLists));
    data.push({ ddql: 0, perm: 0, sort: data.length + 1 });
    this.setState({ serviceLists: data });
  };

  moveRow = (dragIndex, hoverIndex) => {
    console.log(dragIndex, hoverIndex);
    const { serviceLists } = this.state;
    const dragRow = serviceLists[dragIndex];
    console.log(hoverIndex);

    this.setState(
      update(this.state, {
        serviceLists: {
          $splice: [
            [dragIndex, 1],
            [hoverIndex, 0, dragRow],
          ],
        },
      }),
    );
  };

  submitService = () => {
    const params = {
      serveTypeList: this.state.serviceLists,
    };
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        ...params,
      },
      url: `/ddql/shop/type/add`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          this.getData();
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  render() {
    const { serviceLists = [], motionTypelist = [] } = this.state;
    const { thumbnailFileList, carouselFileList, previewImage, previewOpen } = this.state;
    const typeColumns = [
      {
        title: '序号',
        render: (res, record, index) => <div>{index + 1}</div>,
      },
      {
        title: '类型名称',
        dataIndex: 'name',
        render: (res, record, index) => (
          <div>
            <Input
              onChange={(e) => {
                const data = JSON.parse(JSON.stringify(this.state.serviceLists));
                data[index].name = e.target.value;
                this.setState({
                  serviceLists: data,
                });
              }}
              value={record.name}
              placeholder="请输入"
            />
          </div>
        ),
      },
      {
        title: '去美发',
        dataIndex: 'ddql',
        render: (status, record, index) => {
          return (
            <Switch
              unCheckedChildren="关"
              checkedChildren="开"
              checked={status}
              onChange={(value) => {
                const data = JSON.parse(JSON.stringify(this.state.serviceLists));
                data.map((resd, indexs) => {
                  if (index == indexs) {
                    resd.ddql = value ? 1 : 0;
                    if (value) {
                      resd.perm = 0;
                    }
                  } else if (value) {
                    resd.ddql = 0;
                  }
                });
                this.setState({
                  serviceLists: data,
                });
              }}
            />
          );
        },
      },
      {
        title: '去染发',
        dataIndex: 'perm',
        render: (status, record, index) => {
          return (
            <Switch
              unCheckedChildren="关"
              checkedChildren="开"
              checked={status}
              onChange={(value) => {
                const data = JSON.parse(JSON.stringify(this.state.serviceLists));
                data.map((resd, indexs) => {
                  if (index == indexs) {
                    resd.perm = value ? 1 : 0;
                    if (value) {
                      resd.ddql = 0;
                    }
                  } else if (value) {
                    resd.perm = 0;
                  }
                });
                this.setState({
                  serviceLists: data,
                });
              }}
            />
          );
        },
      },
      {
        title: '操作',
        render: (res, record, index) => (
          <div>
            <span
              className="mL15 red"
              onClick={() => {
                if (record.id) {
                  this.props.dispatch({
                    type: 'myModel/getSetData',
                    payload: {
                      deleteId: record.id,
                    },
                    url: `/ddql/shop/type/delete`,
                    method: 'POST',
                    myData: (res) => {
                      if (res && res.code === 10000) {
                        message.success(res.msg);
                        this.getData();
                      } else {
                        message.error(res.msg);
                        // this.setState({ isSelectForm: true });
                      }
                    },
                  });
                } else {
                  const data = JSON.parse(JSON.stringify(this.state.serviceLists));
                  data.splice(index, 1);
                  data.map((resd, index) => {
                    resd.sort = index + 1;
                  });
                  this.setState({
                    serviceLists: data,
                  });
                }
              }}
            >
              删除
            </span>
          </div>
        ),
      },
    ];
    const uploadProps = (type, index) => {
      return {
        name: 'file',
        action: '/ddql/file/upload',

        onChange: (info) => {
          const fileType = [
            'doc',
            'txt',
            'pdf',
            'zip',
            'rar',
            'xls',
            'xlsx',
            'docs',
            'pptx',
            'ppt',
          ];
          if (info.file.status === 'done') {
            console.log(info.file);
            const url = info.file.response.data.uri;
            const { ckeditor } = this;
            const ele = ckeditor.instance.document.createElement('img');
            ele.setAttribute('src', url);

            ckeditor.instance.insertElement(ele);
            // }
          }
        },
      };
    };

    const props = {
      grid: false,
      width: 316,
      height: 246,
      resize: true,
      resizeAndDrag: true,
      modalTitle: '上传图片',
      modalWidth: 600,
    };

    const uploadButton = (
      <button style={{ border: 0, background: 'none' }} type="button">
        <PlusOutlined />
        <div style={{ marginTop: 8 }}>上传</div>
      </button>
    );
    // 图片格式校验（限制为jpg/jpeg/png）
    const beforeUpload = (file) => {
      const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
      if (!isJpgOrPng) {
        message.error('仅支持上传 JPG/PNG 格式的图片！');
        return false;
      }
      return true;
    };

    return (
      <div style={{ paddingTop: 24 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <h1 style={{ fontWeight: '600', fontSize: '18px' }}>基本信息</h1>
          <div
            style={{
              textAlign: 'right',
              paddingRight: 20,
              paddingTop: 15,
              display: 'flex',
              alignItems: 'center',
            }}
          >
            <div
              style={{
                display: 'inline-block',
                padding: '0 25px',
                borderRight: '1px solid #eaeaea',
              }}
            >
              <div style={{ color: '#929292', fontSize: 18 }}>累计充值余额</div>
              <div style={{ fontSize: 18, color: '#1890FF' }}>
                ¥{((this.state.rechargeOrderCountData.amount || 0) - (this.state.rechargeOrderCountData.refundAmount || 0)) / 100}
              </div>
            </div>
            <div
              style={{
                display: 'inline-block',
                padding: '0 25px',
              }}
            >
              <div style={{ color: '#929292', fontSize: 18 }}>当前余额</div>
              <div style={{ fontSize: 18, color: '#1890FF' }}>￥{this.state.teamHealthyCoin}</div>
            </div>
            <Button style={{ marginLeft: 30, marginRight: 15 }} onClick={() => history.goBack()}>
              返回
            </Button>
          </div>
        </div>
        <Form
          ref={this.formRef}
          layout="vertical"
          wrapperCol={{
            span: 7,
          }}
          initialValues={{
            remember: true,
          }}
          onFinish={this.onFinish}
          autoComplete="off"
        >
          <Form.Item label="团体名称" name="name" rules={[{ required: true, message: '请输入!' }]}>
            <Input placeholder="请输入" />
          </Form.Item>

          <Form.Item
            label={
              <div>
                <span style={{ color: 'red' }}>*</span>团体类型
              </div>
            }
          >
            <div style={{ display: 'flex', width: '100%' }}>
              <Form.Item name="type" noStyle>
                <Select
                  className="norBorder"
                  placeholder="请选择"
                  style={{
                    flex: 1,
                    borderTopRightRadius: '0!important',
                    borderBottomRightRadius: '0!important',
                    borderRight: 'none !important',
                  }}
                  onChange={(e) => {
                    this.setState({
                      teamType: e,
                    });
                    if (this.props.onTypeChange) {
                      this.props.onTypeChange(e);
                    }
                  }}
                >
                  <Option value={0}>企事单位</Option>
                  <Option value={1}>政府部门</Option>
                  <Option value={2}>家庭</Option>
                  <Option value={3}>朋友运动群</Option>
                </Select>
              </Form.Item>
            </div>
          </Form.Item>
          <Form.Item label="地区" name="region" rules={[{ required: true, message: '请选择或搜索!' }]}>
            <Cascader
              options={this.state.regionOptions || []}
              placeholder="请选择或搜索"
              showSearch
            />
          </Form.Item>
          <Form.Item
            label="详细地址"
            name="address"
            rules={[{ required: false, message: '请输入!' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item>
          <Form.Item
            label="联系人名称"
            name="contactPerson"
            rules={[{ required: true, message: '请输入!' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item>
          <Form.Item
            label="联系电话"
            name="contactPhone"
            rules={[
              { required: true, message: '请输入!' },
              { pattern: /^(\d{7}|\d{8}|\d{11})$/, message: '请输入11位、7位或8位电话' }
            ]}
          >
            <Input placeholder="请输入" />
          </Form.Item>
          <Form.Item
            label="邮箱"
            name="contactEmail"
            rules={[{ required: false, message: '请输入!' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item>
          {(this.state.teamType == 0 || this.state.teamType == 1) && (
            <Form.Item
              label="是否多部门管理"
              name="isMultiDepartment"
              rules={[{ required: true, message: '请输入!' }]}
            >
              <Radio.Group>
                <Radio value={0}>否</Radio>
                <Radio value={1}>是</Radio>
              </Radio.Group>
            </Form.Item>
          )}
          <Form.Item>
            <Button style={{ marginRight: 20 }} onClick={() => history.goBack()}>
              取消
            </Button>
            <Button type="primary" htmlType="submit">
              提交
            </Button>
          </Form.Item>
        </Form>
        <Modal
          title="选择位置"
          visible={this.state.map}
          onOk={this.handleOkz}
          onCancel={this.handleCancelz}
          style={{ paddingBottom: 50 }}
        >
          <div style={{ width: '100%', height: '400px', marginBottom: 50 }}>
            <input
              onBlur={this.getAddress}
              onChange={this.input}
              id="tipinput"
              style={{ marginBottom: 20, width: '60%' }}
            />
            <Map
              events={this.selectAddress}
              plugins={['Autocomplete']}
              amapkey={'788e08def03f95c670944fe2c78fa76f'}
              zoom={14}
              style={{ marginBottom: 50 }}
              center={{
                longitude: this.state.longitude,
                latitude: this.state.latitude,
              }}
            />
          </div>
        </Modal>
        <Modal
          style={{ minWidth: '50%' }}
          open={this.state.addGroupModalVisible}
          onOk={this.submitService}
          onCancel={this.handleCancel}
          title="新增门店服务类型"
        // footer={[]}
        >
          <Alert message="按住鼠标拖拽可调整展示顺序" showIcon style={{ textAlign: 'left' }} />
          <div className="modal-wrapper">
            <DndProvider backend={HTML5Backend}>
              <Table
                defaultSize="large"
                style={{ paddingTop: 25 }}
                columns={typeColumns}
                dataSource={this.state.serviceLists}
                search={false}
                options={false}
                pagination={false}
                components={this.components}
                onRow={(record, index) => ({
                  index,
                  moveRow: this.moveRow,
                })}
                scroll={{ y: 630 }}
              />
            </DndProvider>
            <Button onClick={this.addGroup} type="dashed" style={{ marginTop: 20, width: '100%' }}>
              + 添加服务类型
            </Button>
          </div>
        </Modal>
      </div>
    );
  }
}
export default connect()(Login);
