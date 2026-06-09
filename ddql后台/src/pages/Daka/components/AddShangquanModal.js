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
  InputNumber,
} from 'antd';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { history, connect, Link } from 'umi';
import ImgCrop from 'antd-img-crop';
import CKEditor from 'react-ckeditor-wrapper';
import { HTML5Backend } from 'react-dnd-html5-backend';
import update from 'immutability-helper';
import moment from 'moment';
import dayjs from 'dayjs';
import { urlName } from '@/utils/utils';
const { TextArea } = Input;
import Map from '@/components/Map';
// import { setToken } from '@/utils/authority';

const { Option } = Select;
const { RangePicker } = DatePicker;
// 场所基础信息

class AddShangquanModal extends React.Component {
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
  };

  componentDidMount() {
    const { edit, id } = this.props;
    if (!this.props.isAdd) {
      const values = this.props.info;
      const carouselFileList = [];
      const carouselImageUrls = values.images?.split(';');
      carouselImageUrls?.map((ress, index) => {
        carouselFileList.push({
          uid: String(index + 1),
          name: `image${index}.png`,
          status: 'done',
          url: ress,
          response: { data: { url: ress } },
        });
      });
      console.log(carouselFileList);
      this.formRef.current.setFieldsValue({
        name: values.name,
        checkInMethod: values.checkInMethod,
        checkInDistance: values.checkInDistance ? values.checkInDistance : 0,
        checkInTypeId: values.checkInTypeId,
        contactPhone: values.contactPhone,
        sort: values.sort,
        status: values.status == 0 ? true : false,
        introduction: values.introduction,
        remark: values.remark,
        userIds: values.userIds,
        introduction: values.introduction,
      });
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          searchId: values.id,
        },
        url: `/ddql/checkInPlace/selectUserByPlaceId`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code == 10000) {
            this.formRef.current.setFieldsValue({
              userIds: res.data.map((xz) => xz.id),
            });
          } else {
            message.error(res.msg);
            // this.setState({ isSelectForm: true });
          }
        },
      });
      this.setState({
        locationInfo: { address: values.address },
        locationStr: values.location,
        locationValue: values.address,
        carouselFileList,
        checkInMethod: values.checkInMethod,
      });
    }
  }

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
    if (!this.state.locationInfo) {
      message.info('请选择所在位置');
      return;
    }
    this.setState(
      {
        map: false,
      },
      () => {
        this.setState({
          locationValue: this.state.locationInfo.address + this.state.locationInfo.name,
        });
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
        placeSearch.search(document.getElementById('tipinputs').value, (res, result) => {
          const { pois = [] } = result.poiList;
          if (pois.length == 0) {
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
              location: `${result.poiList.pois[0].location.lng},${result.poiList.pois[0].location.lat}`,
            },
            () => {
              console.log(this.state.locations);
            },
          );
        });
      });
    }, 150);
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

  // 通用的文件变化处理
  handleUploadChange =
    (type) =>
    ({ file, fileList }) => {
      this.setState({ [type]: fileList }, () => {
        const { response = {} } = file;
        if (response.code == 10000) {
          const data = this.state[type];
          if (data[data.length - 1]) {
            data[data.length - 1].response.data.url =
              urlName + data[data.length - 1].response.data.url;
            this.setState({
              [type]: data,
            });
          } else {
            this.setState({
              [type]: [],
            });
          }
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
    this.formRef.current.validateFields().then((values) => {
      if (this.state.carouselFileList.length == 0) {
        message.info('请上传场地图片');
        return;
      }
      const params = {
        location: this.state.locationStr,
        name: values.name,
        checkInMethod: values.checkInMethod,
        checkInDistance: values.checkInDistance ? values.checkInDistance : 0,
        checkInTypeId: values.checkInTypeId,
        contactPhone: values.contactPhone,
        address: this.state.locationValue,
        sort: values.sort,
        introduction: values.introduction,
        remark: values.remark,
        userIds: values.userIds,
        status: values.status ? 0 : 1,
        introduction: values.introduction,
        images: this.state.carouselFileList.map((re) => re.response.data.url),
      };
      if (!this.props.isAdd) {
        params.id = this.props.info.id;
      }
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          ...params,
        },
        url: this.props.isAdd ? `/ddql/checkInPlace/add` : `/ddql/checkInPlace/update`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.props.getData();
            this.props.handleCancel();
          } else {
            message.error(res.msg);
            // this.setState({ isSelectForm: true });
          }
        },
      });
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
      width: 600,
      height: 250,
      resize: true,
      resizeAndDrag: true,
      modalTitle: '上传图片',
      modalWidth: 800,
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
      <Modal
        zIndex={101}
        style={{ minWidth: '50%' }}
        open
        onOk={this.submitService}
        onCancel={() => this.props.handleCancel()}
        title={this.props.isAdd ? '新增打卡点' : '编辑打卡点'}
        // footer={[]}
      >
        <Form
          ref={this.formRef}
          labelCol={{
            span: 4,
          }}
          wrapperCol={{
            span: 16,
          }}
          initialValues={{
            remember: true,
          }}
          autoComplete="off"
        >
          {/* 预览模态框 */}
          <Modal open={previewOpen} footer={null} onCancel={this.handleCancelPreview}>
            <img alt="预览" style={{ width: '100%' }} src={previewImage} />
          </Modal>
          <Form.Item label="场地名称" name="name" rules={[{ required: true, message: '请输入!' }]}>
            <Input placeholder="请输入" />
          </Form.Item>
          <Form.Item
            label={
              <div>
                <span style={{ color: 'red', paddingRight: 4 }}>*</span>场地地址
              </div>
            }
            name="address"
            rules={[{ required: false, message: '请选择!' }]}
          >
            <div style={{ display: 'flex', width: '100%' }}>
              <Input
                disabled
                value={this.state.locationValue}
                placeholder="请选择位置"
                style={{ marginRight: 8 }}
              ></Input>
              <Button type="default" className="darkBlue-btn" onClick={this.showModal}>
                选择位置
              </Button>
            </div>
          </Form.Item>
          <Form.Item label="打卡方式" name="checkInMethod" rules={[{ required: true }]}>
            <Select onChange={(e) => this.setState({ checkInMethod: e })} placeholder="请选择">
              <Option value={0}>扫码打卡</Option>
              <Option value={1}>距离打卡</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label={
              <div>
                <span style={{ color: 'red', paddingRight: 4 }}>*</span>打卡距离
              </div>
            }
          >
            <Form.Item
              noStyle
              name="checkInDistance"
              rules={[{ required: true, message: '请选择!' }]}
            >
              <InputNumber placeholder="请输入" />
            </Form.Item>
            <span style={{ paddingLeft: 6 }}>米</span>
          </Form.Item>
          <Form.Item
            label="打卡类型"
            name="checkInTypeId"
            rules={[{ required: true, message: '请输入!' }]}
          >
            <Select placeholder="请选择">
              {this.props.gymTypelist.map((xz) => (
                <Option value={xz.id}>{xz.name}</Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            label="场地管理员"
            name="userIds"
            rules={[{ required: true, message: '请输入!' }]}
          >
            <Select mode="multiple" placeholder="请选择">
              {this.props.adminList.map((xz) => (
                <Option value={xz.id}>
                  {xz.nickname}（{xz.phone}）
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            label="场地联系电话"
            name="contactPhone"
            rules={[{ required: true, message: '请输入!' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item>

          <Form.Item
            label="启用状态"
            name="status"
            rules={[{ required: true, message: '请选择!' }]}
            valuePropName="checked"
            initialValue={true}
          >
            <Switch checkedChildren="开启" unCheckedChildren="关闭" />
          </Form.Item>
          <Form.Item label="排序" name="sort" rules={[{ required: true, message: '请选择!' }]}>
            <InputNumber placeholder="请输入" />
          </Form.Item>
          <Form.Item label="场地介绍" name="introduction">
            <TextArea rows={4} placeholder="请输入" />
          </Form.Item>
          <Form.Item
            label={
              <span>
                <span style={{ color: 'red', paddingRight: 4 }}>*</span>场地图片
              </span>
            }
          >
            <Form.Item noStyle>
              <ImgCrop {...props}>
                <Upload
                  action="/ddql/file/upload"
                  listType="picture-card"
                  fileList={carouselFileList}
                  onChange={this.handleUploadChange('carouselFileList')}
                  onPreview={this.handlePreview}
                  beforeUpload={beforeUpload}
                  accept="image/jpeg,image/png"
                  headers={{ token: localStorage.getItem('token') }}
                >
                  {carouselFileList.length < 5 && uploadButton}
                </Upload>
              </ImgCrop>
            </Form.Item>
            <span style={{ color: '#ccc' }}>
              支持上传最多五张图，支持在线裁剪尺寸，图片格式支持jpg/jpeg/png
            </span>
          </Form.Item>
          <Form.Item label="备注" name="remark">
            <TextArea rows={4} placeholder="请输入" />
          </Form.Item>
        </Form>
        {this.state.map && (
          <Modal
            title="选择位置"
            visible
            onOk={this.handleOkz}
            zIndex={102}
            onCancel={this.handleCancelz}
            style={{ paddingBottom: 50, minWidth: 800 }}
          >
            <div style={{ width: '100%', marginBottom: 50 }}>
              <Map
                locationName={this.state.locationStr}
                locationInfo={this.state.locationInfo}
                isAdd={this.props.isAdd}
                onLocationChange={(locationInfo) => {
                  console.log('收到地图数据:', locationInfo);
                  const locationStr =
                    locationInfo && locationInfo.location.lat && locationInfo.location.lng
                      ? `${locationInfo.location.lng},${locationInfo.location.lat}`
                      : '';
                  console.log('收到地图数据:', locationStr);
                  this.setState({
                    locationInfo, // 存储地图数据
                    locationStr,
                  });
                }}
              />
            </div>
          </Modal>
        )}
      </Modal>
    );
  }
}
export default connect()(AddShangquanModal);
