import Map from '@/components/Map';
import { post } from '@/utils/request';
import { debounce, urlName } from '@/utils/utils';
import { MinusCircleOutlined, PlusOutlined } from '@ant-design/icons';
import { history } from '@umijs/max';
import {
  Alert,
  Button,
  Col,
  Form,
  Input,
  InputNumber,
  message,
  Modal,
  Popconfirm,
  Radio,
  Row,
  Select,
  Spin,
  Switch,
  Table,
  TimePicker,
  Upload,
} from 'antd';
import ImgCrop from 'antd-img-crop';
import dayjs from 'dayjs';
import update from 'immutability-helper';
import React from 'react';
import CKEditor from 'react-ckeditor-wrapper';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
const { TextArea } = Input;

const { Option } = Select;
// 场所基础信息

let dragingIndex = -1;
class BodyRow extends React.Component {
  state = {
    updataCloneList: [],
  };

  render() {
    const {
      isOver,
      connectDragSource,
      connectDropTarget,
      moveRow,
      ...restProps
    } = this.props;
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
      connectDropTarget(
        <tr {...restProps} className={className} style={style} />,
      ),
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
    if (dragIndex == hoverIndex) {
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

class AddShopMsgModal extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    pageNum: 1,
    list: [],
    thumbnailFileList: [],
    carouselFileList: [],
    locations: {},
    longitude: 113.880469,
    latitude: 22.889404,
    addGroupModalVisible: false,
    majorList: [],
    managerList: [],
    reviewVisible: false,
    searchType: 1,
    customerCodeImgList: [],
    shopStatus: undefined,
    cropAspect: 1 / 1,
  };

  components = {
    body: {
      row: DragableBodyRow,
    },
  };

  componentDidMount() {
    const { type, id, disabled = false, auditId = undefined } = this.props;
    this.setState(
      {
        type,
        id,
        disabled,
        auditId,
      },
      () => {
        // 商家入驻审核 是小程序的商家数据
        type != 'add' && this.getData();
        id && type != 'review' && type != 'info' && this.getManagerList();
      },
    );
    this.getBusinessType();
  }

  getManagerList = async () => {
    const res = await post(`/guzhe/shop/manager/get`, {
      searchField1: this.state.id,
    });
    if (res && res.code == 10000) {
      this.setState({
        managerList: res.data,
      });
    } else {
      message.error(res?.msg);
    }
  };

  changeManager = async (item) => {
    // 区分 审核修改 和 编辑修改
    if (this.state.type == 'review') {
      // 审核修改
      return;
    }
    // 新增或修改管理员时，姓名和手机号都要填，且存在商家
    if (!this.state.id || !item.phone || !item.name) {
      return;
    }

    console.log(item, this.state.id);

    const res = await post(
      item?.id ? '/guzhe/shop/manager/update' : '/guzhe/shop/manager/add',
      {
        ...item,
        shopId: this.state.id,
        headManager: item.sort == 1 ? 1 : 0,
      },
    );

    if (res && res.code == 10000) {
      message.success('操作成功');
      this.getManagerList();
    } else {
      message.error(res?.msg);
      this.getManagerList();
    }
  };

  getData = async () => {
    this.setState({
      spinning: true,
    });
    const res = await post(
      this.state.type == 'edit'
        ? `/guzhe/shop/selectById`
        : `/guzhe/shop/audit/detail`,
      {
        searchId: this.state.id,
      },
    );
    this.setState({
      spinning: false,
    });
    if (res && res.code == 10000) {
      const values = this.state.type == 'edit' ? res.data.shop : res.data;
      const carouselFileList = [];
      const carouselImageUrls = values.galleryImages;
      carouselImageUrls.map((ress, index) => {
        carouselFileList.push({
          uid: String(index + 1),
          name: `image${index}.png`,
          status: 'done',
          url: ress,
          response: { data: { url: ress } },
        });
      });
      const industryCategoryIds =
        this.state.type == 'edit'
          ? res.data.industryList
          : res.data.industryCategories;
      this.formRef.current.setFieldsValue({
        status: values.status == 1 ? true : false,
        name: values.name,
        userName: values.userName ? values.userName : '',
        recommendOrder: values.recommendOrder ? values.recommendOrder : 0,
        phone: values.phone ? values.phone : '',
        customerPhone: values.customerPhone ? values.customerPhone : '',
        sortOrder: values.sortOrder,
        // time: values.startTime
        //   ? [dayjs(values.startTime, 'HH:mm'), dayjs(values.endTime, 'HH:mm')]
        //   : undefined,
        businessTime: values.businessTime,
        circleIds:
          (this.state.type == 'edit'
            ? res.data?.circleList?.map((x) => x.circleId)
            : res.data?.circleIds) || [],
        industryCategoryIds: industryCategoryIds.map(
          (x) => x.industryCategoryId,
        ),
      });
      this.setState({
        location: values.location,
        id: values.id,
        thumbnailFileList: values.coverImageUrl
          ? [
              {
                uid: '1',
                name: 'image.png',
                status: 'done',
                url: values.coverImageUrl,
                response: { data: { url: values.coverImageUrl } },
              },
            ]
          : [],
        customerCodeImgList: values.customerCodeImg
          ? [
              {
                uid: '1',
                name: 'image.png',
                status: 'done',
                url: values.customerCodeImg,
                response: { data: { url: values.customerCodeImg } },
              },
            ]
          : [],
        carouselFileList: carouselFileList,
        content: values.description,
        locationInfo: { address: values.address },
        locationStr: values.location,
        locationValue: values.address,
        shopStatus: values.shopStatus,
        managerList:
          this.state.type == 'edit' ? this.state.managerList : values.managers,
      });
    } else {
      message.error(res?.msg);
    }
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
          locationValue:
            this.state.locationInfo.address + this.state.locationInfo.name,
        });
      },
    );
  };

  getBusinessType = async () => {
    const res = await post('/guzhe/shop/cate/get');
    if (res && res.code == 10000) {
      this.setState({ majorList: res.data });
    } else {
      message.error(res?.msg);
    }
  };

  review = (handleParam = {}) => {
    this.formRefs.current.validateFields().then(async (values) => {
      const res = await post('/guzhe/shop/audit/handle', {
        id: this.state.id,
        auditStatus: values.searchType,
        rejectReason: values.keyword,
        ...handleParam,
        managers: this.state.managerList.map((i, index) => ({
          ...i,
          headManager: index == 0 ? 1 : 0, // 是否是主管理员
        })),
      });
      if (res && res.code == 10000) {
        message.success(res.msg);
        this.setState({
          reviewVisible: false,
        });
        setTimeout(() => {
          history.back();
        }, 1000);
      } else {
        message.error(res?.msg);
      }
    });
  };

  // 通用的文件变化处理
  handleUploadChange =
    (type) =>
    ({ file, fileList }) => {
      const list = fileList.filter(
        (i) => i.status == 'done' || i.status == 'uploading',
      );
      this.setState({ [type]: fileList }, () => {
        const { response = {} } = file;
        if (response.code == 10000 && file.status != 'removed') {
          const data = this.state[type];
          if (data[data.length - 1]) {
            data[data.length - 1].response.data.url =
              urlName + data[data.length - 1].response.data.url;
            this.setState({
              [type]: data,
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

  // 裁剪前获取图片尺寸并计算宽高比
  handleBeforeCrop = (file) => {
    return new Promise((resolve) => {
      const img = new Image();
      img.onload = () => {
        const aspect = img.width / img.height;
        this.setState({ cropAspect: aspect });
        URL.revokeObjectURL(img.src);
        resolve(true);
      };
      img.onerror = () => {
        resolve(true);
      };
      img.src = URL.createObjectURL(file);
    });
  };

  addGroup = (typeList) => {
    const data = JSON.parse(JSON.stringify(this.state[typeList]));
    data.push({ sort: data.length + 1 });
    this.setState({ [typeList]: data });
  };

  moveType = async () => {
    const data = structuredClone(this.state.majorList);
    const res = await post('/guzhe/shop/cate/update/sort', {
      searchIds: data.filter((i) => i.id).map((i) => i.id),
    });
    if (res && res.code == 10000) {
      message.success(res.msg);
      this.getBusinessType();
    } else {
      message.error(res?.msg);
    }
  };

  moveRow = (dragIndex, hoverIndex) => {
    console.log(dragIndex, hoverIndex);
    const { majorList } = this.state;
    const dragRow = majorList[dragIndex];
    // 判断移动是否有id
    this.setState(
      update(this.state, {
        majorList: {
          $splice: [
            [dragIndex, 1],
            [hoverIndex, 0, dragRow],
          ],
        },
      }),
      () => {
        if (dragRow?.id) {
          this.moveType();
        }
      },
    );
  };

  onFinish = () => {
    if(!this.state.customerCodeImgList.length){
      message.info('请上传微信二维码');
      return;
    }
    this.formRef.current.validateFields().then(async (values) => {
      const params = {
        coverImageUrl: this.state.thumbnailFileList.map(
          (cz) => cz.response.data.url,
        )[0],
        customerCodeImg: this.state.customerCodeImgList.map(
          (cz) => cz.response.data.url,
        )[0] ||'',
        galleryImages: this.state.carouselFileList.map(
          (cz) => cz.response.data.url,
        ),
        status: values.status ? 1 : 0,
        name: values.name,
        location: this.state.locationStr,
        userName: values.userName ? values.userName : '',
        topRecommend: 0,
        topConsumption: 0,
        recommendOrder: values.recommendOrder ? values.recommendOrder : 0,
        phone: values.phone ? values.phone : '',
        customerPhone: values.customerPhone ? values.customerPhone : '',
        address: this.state.locationValue,
        description: this.state.content,
        sortOrder: values.sortOrder,
        // startTime: values.time ? values.time[0].format('HH:mm:00') : undefined,
        // endTime: values.time ? values.time[1].format('HH:mm:00') : undefined,
        businessTime: values.businessTime,
        circleIds: values.circleIds,
        industryCategoryIds: values.industryCategoryIds,
        id: this.state.id || undefined,
        sort: values.recommendOrder ? values.recommendOrder : 0,
      };

      console.log(this.state.managerList);

      // 判断如果是 review，则调review函数
      if (this.state.type == 'review') {
        this.review(params);
        return;
      }

      const res = await post('/guzhe/shop/save', params);
      if (res && res.code == 10000) {
        message.success(res.msg);
        // 新增时，添加管理员
        if (this.state.type == 'add') {
          await post('/guzhe/shop/manager/add', {
            sort: 1,
            shopId: res.data,
            name: values.userName,
            phone: values.phone,
            headManager: 1,
          });
          history.back();
          return;
        }
        this.getData();
        this.getManagerList();
        this.props.editShopName(values.name);
      } else {
        message.error(res?.msg);
      }
    });
  };

  showReview = () => {
    this.setState({ reviewVisible: true });
  };

  getUpdateType = debounce(async (value, id = undefined) => {
    if (!value) {
      message.error('行业类别名称不能为空');
      return;
    }

    const res = await post(
      id ? '/guzhe/shop/cate/update' : '/guzhe/shop/cate/add',
      {
        id,
        name: value,
      },
    );
    if (res && res.code == 10000) {
      message.success(res.msg);
      this.getBusinessType();
    } else {
      message.error(res?.msg);
    }
  });

  handleDelete = async () => {
    const res = await post('/guzhe/shop/off', {
      changeId: this.state.id,
    });
    if (res && res.code == 10000) {
      message.success(res.msg);
      this.getData();
    } else {
      message.error(res?.msg);
    }
  };

  render() {
    const {
      thumbnailFileList,
      carouselFileList,
      previewImage,
      previewOpen,
      type,
      disabled,
      customerCodeImgList,
    } = this.state;
    const typeColumns = [
      {
        title: '序号',
        render: (res, record, index) => <div>{index + 1}</div>,
      },
      {
        title: '行业类别名称',
        dataIndex: 'name',
        render: (res, record, index) => (
          <div>
            <Input
              onChange={(e) => {
                const data = JSON.parse(JSON.stringify(this.state.majorList));
                data[index].name = e.target.value;
                this.setState({
                  majorList: data,
                });
              }}
              onBlur={(e) => this.getUpdateType(e.target.value, record?.id)}
              value={record.name}
              placeholder="请输入"
            />
          </div>
        ),
      },
      {
        title: '操作',
        render: (res, record, index) => (
          <Popconfirm
            title={
              <>
                <div>删除提示</div>
                <div>
                  <span style={{ color: 'red' }}>删除的内容不可恢复</span>，
                  <span style={{ color: '#ccc' }}>确定删除吗？</span>
                </div>
              </>
            }
            onConfirm={async () => {
              if (record.id) {
                const res = await post('/guzhe/shop/cate/del', {
                  searchIds: [record.id],
                });
                if (res && res.code == 10000) {
                  message.success(res.msg);
                  this.getBusinessType();
                } else {
                  message.error(res.msg);
                }
              } else {
                const data = JSON.parse(JSON.stringify(this.state.majorList));
                data.splice(index, 1);
                data.map((resd, index) => {
                  resd.sort = index + 1;
                });
                this.setState({
                  majorList: data,
                });
              }
            }}
            // onCancel={cancel}
            okText="是"
            cancelText="否"
          >
            <span className="mL15 red">删除</span>
          </Popconfirm>
        ),
      },
    ];
    const managerColumns = [
      {
        title: '序号',
        render: (res, record, index) => <div>{index + 1}</div>,
      },
      {
        title: '姓名',
        dataIndex: 'name',
        render: (res, record, index) => (
          <div>
            <Input
              onChange={(e) => {
                const data = JSON.parse(JSON.stringify(this.state.managerList));
                data[index].name = e.target.value;
                this.setState({
                  managerList: data,
                });
              }}
              onBlur={(e) => this.changeManager(record)}
              value={record.name}
              placeholder="请输入"
            />
          </div>
        ),
      },
      {
        title: '手机号',
        dataIndex: 'phone',
        render: (res, record, index) => (
          <div>
            <Input
              onChange={(e) => {
                const data = JSON.parse(JSON.stringify(this.state.managerList));
                data[index].phone = e.target.value;
                this.setState({
                  managerList: data,
                });
              }}
              onBlur={(e) => this.changeManager(record)}
              onClick={(e) => {
                // 判断是否是修改店长手机号且店长已存在
                if (!index && record.id) {
                  e.preventDefault();
                  e.target.blur();
                  Modal.confirm({
                    content:
                      '编辑店长手机号，那么此商家的权限就会变更到新手机号的用户小程序上，是否确定继续操作？',
                    centered: true,
                    onOk: () => {
                      e.target.focus();
                    },
                  });
                }
              }}
              value={record.phone}
              placeholder="请输入"
            />
          </div>
        ),
      },
      {
        title: '操作',
        render: (res, record, index) =>
          index != 0 && (
            <Popconfirm
              title={
                <>
                  <div>删除提示</div>
                  <div>
                    <span style={{ color: 'red' }}>删除的内容不可恢复</span>，
                    <span style={{ color: '#ccc' }}>确定删除吗？</span>
                  </div>
                </>
              }
              onConfirm={async () => {
                if (record.id && this.state.type != 'review') {
                  const res = await post('/guzhe/shop/manager/del', {
                    deleteIds: [record.id],
                  });
                  if (res && res.code == 10000) {
                    message.success(res.msg);
                    this.getManagerList();
                  } else {
                    message.error(res?.msg);
                  }
                } else {
                  const data = JSON.parse(
                    JSON.stringify(this.state.managerList),
                  );
                  data.splice(index, 1);
                  data.map((resd, index) => {
                    resd.sort = index + 1;
                  });
                  this.setState({
                    managerList: data,
                  });
                }
              }}
              // onCancel={cancel}
              okText="是"
              cancelText="否"
            >
              <Button
                icon={<MinusCircleOutlined />}
                color="danger"
                variant="text"
              />
            </Popconfirm>
          ),
      },
    ];
    const uploadProps = (type, index) => {
      return {
        name: 'file',
        action: '/guzhe/file/upload',

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
          if (info.file.status == 'done') {
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
      resize: true,
      resizeAndDrag: true,
      modalTitle: '上传图片',
      modalWidth: 600,
      beforeCrop: this.handleBeforeCrop,
      aspect: this.state.cropAspect,
    };

    const uploadButton = (
      <button style={{ border: 0, background: 'none' }} type="button">
        <PlusOutlined />
        <div style={{ marginTop: 8 }}>上传</div>
      </button>
    );
    // 图片格式校验（限制为jpg/jpeg/png）
    const beforeUpload = (file) => {
      const isJpgOrPng = file.type == 'image/jpeg' || file.type == 'image/png';
      if (!isJpgOrPng) {
        message.error('仅支持上传 JPG/PNG 格式的图片！');
      }
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
        message.error('图片大小不能超过 2MB!');
      }
      return isJpgOrPng && isLt2M;
    };

    return (
      <Spin spinning={this.state.spinning}>
        <Row align="middle" justify="space-between">
          <Col span={4}>
            <h2>商家信息</h2>
          </Col>
          <Col
            span={6}
            style={{
              display: 'flex',
              gap: 20,
              justifyContent: 'flex-end',
              paddingRight: 20,
            }}
          >
            {(this.state.shopStatus === 0 || this.state.shopStatus === 1) && (
              <Button type="primary" danger onClick={this.handleDelete}>
                注销
              </Button>
            )}
            <Button onClick={() => history.back()}>返回</Button>
          </Col>
        </Row>
        <Form
          ref={this.formRef}
          labelCol={{
            span: 2,
          }}
          wrapperCol={{
            span: 10,
          }}
          initialValues={{
            remember: true,
          }}
          onFinish={this.onFinish}
          autoComplete="off"
          disabled={disabled}
        >
          {/* 缩略图上传 */}
          <Form.Item
            label={
              <span>
                <span style={{ color: 'red' }}>*</span>商家logo
              </span>
            }
          >
            <Form.Item noStyle>
              <ImgCrop {...props} aspect={4 / 2}>
                <Upload
                  disabled={disabled}
                  action="/guzhe/file/upload"
                  listType="picture-card"
                  fileList={thumbnailFileList}
                  onChange={this.handleUploadChange('thumbnailFileList')}
                  onPreview={this.handlePreview}
                  beforeUpload={beforeUpload}
                  accept="image/jpeg,image/png"
                  headers={{ token: localStorage.getItem('token') }}
                >
                  {thumbnailFileList.length < 1 && uploadButton}
                </Upload>
              </ImgCrop>
            </Form.Item>
            <span style={{ color: '#ccc' }}>
              支持在线裁剪尺寸，图片格式支持jpg/jpeg/png
            </span>
          </Form.Item>

          {/* 轮播图上传 */}
          <Form.Item
            label={
              <span>
              商家轮播图
              </span>
            }
          >
            <Form.Item noStyle>
              <ImgCrop {...props}>
                <Upload
                  disabled={disabled}
                  action="/guzhe/file/upload"
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

          {/* 预览模态框 */}
          <Modal
            open={previewOpen}
            footer={null}
            onCancel={this.handleCancelPreview}
          >
            <img alt="预览" style={{ width: '100%' }} src={previewImage} />
          </Modal>
          <Form.Item
            label={
              <div>
                <span style={{ color: 'red', paddingRight: 4 }}>*</span>商家名称
              </div>
            }
          >
            <Form.Item noStyle name="name">
              <Input placeholder="请输入" />
            </Form.Item>
            <span style={{ color: '#ccc' }}>商家名称唯一</span>
          </Form.Item>

          <Form.Item
            label="企业位置"
            name="location"
            rules={[{ required: false, message: '请选择!' }]}
          >
            <div style={{ display: 'flex', width: '100%' }}>
              <Input
                disabled
                value={this.state.locationValue}
                placeholder="请选择位置"
                style={{ marginRight: 8 }}
              ></Input>
              <Button
                type="default"
                className="darkBlue-btn"
                onClick={this.showModal}
              >
                选择位置
              </Button>
            </div>
          </Form.Item>
          {/* <Form.Item
            label="所属商超"
            name="circleIds"
            rules={[{ required: false, message: '请选择!' }]}
          >
            <Select
              mode="multiple"
              placeholder="请选择"
              showSearch
              optionFilterProp="children"
            >
              {this.props.circleList.map((sxsa) => (
                <Option value={sxsa.id}>{sxsa.name}</Option>
              ))}
            </Select>
          </Form.Item> */}
          <Form.Item
            label={
              <div>
                <span style={{ color: 'red' }}>*</span>行业类别
              </div>
            }
            rules={[{ required: true, message: '请选择!' }]}
          >
            <div style={{ display: 'flex', width: '100%' }}>
              <Form.Item
                name="industryCategoryIds"
                noStyle
                rules={[{ required: true, message: '请选择!' }]}
              >
                <Select
                  mode="multiple"
                  className="norBorder"
                  placeholder="请选择"
                  style={{ flex: 1 }}
                >
                  {this.state.majorList.map((sa) => (
                    <Option key={sa.id} value={sa.id}>
                      {sa.name}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
              <Button
                className="nolBorder"
                type="primary"
                onClick={() => this.setState({ addGroupModalVisible: true })}
              >
                新增分类
              </Button>
            </div>
          </Form.Item>
          <Form.Item
            label="店长"
            name="userName"
            rules={[{ required: true, message: '请选择!' }]}
          >
            <Input
              placeholder="请输入"
              onChange={(e) => {
                if (type != 'add') return;
                const data = structuredClone(this.state.managerList);
                const value = e.target.value;
                // 存在商家管理人员，直接修改
                if (data.length > 0) {
                  data[0].name = value;
                  this.setState({ managerList: data });
                } else {
                  // 没有商家管理人员，新增一个
                  this.setState({ managerList: [{ name: value, sort: 1 }] });
                }
              }}
            />
          </Form.Item>
          <Form.Item
            label="联系电话"
            name="phone"
            rules={[{ required: true, message: '请选择!' }]}
          >
            <Input
              placeholder="请输入"
              onChange={(e) => {
                if (type != 'add') return;
                const data = structuredClone(this.state.managerList);
                const value = e.target.value;
                // 存在商家管理人员，直接修改
                if (data.length > 0) {
                  data[0].phone = value;
                  this.setState({ managerList: data });
                } else {
                  // 没有商家管理人员，新增一个
                  this.setState({ managerList: [{ phone: value, sort: 1 }] });
                }
              }}
            />
          </Form.Item>
          <Form.Item
            label="营业时间"
            name="businessTime"
            rules={[{ required: false, message: '请选择!' }]}
          >
            {/* <TimePicker.RangePicker format="HH:mm" /> */}
            <Input placeholder="请输入" />
          </Form.Item>
          <Form.Item
            label={
              <div>
                <span style={{ color: 'red' }}>*</span>商家管理人员
              </div>
            }
            rules={[{ required: true, message: '请选择!' }]}
          >
            <Table
              columns={managerColumns}
              dataSource={this.state.managerList}
              pagination={false}
              rowKey="sort"
            />
            <Button
              onClick={() => this.addGroup('managerList')}
              type="dashed"
              disabled={disabled || this.state.managerList.length > 3}
              style={{ marginTop: 20, width: '100%' }}
            >
              + 添加
            </Button>
          </Form.Item>
          <Form.Item
            label="客服电话"
            name="customerPhone"
            rules={[{ required: false, message: '请选择!' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item>
          {/* 缩略图上传 */}
          <Form.Item
            label={
              <div>
                <span style={{ color: 'red' }}>*</span>微信二维码
              </div>
            }
          >
            <Form.Item noStyle>
              <ImgCrop {...props}>
                <Upload
                  disabled={disabled}
                  action="/guzhe/file/upload"
                  listType="picture-card"
                  fileList={customerCodeImgList}
                  onChange={this.handleUploadChange('customerCodeImgList')}
                  onPreview={this.handlePreview}
                  beforeUpload={beforeUpload}
                  accept="image/jpeg,image/png"
                  headers={{ token: localStorage.getItem('token') }}
                >
                  {customerCodeImgList.length < 1 && uploadButton}
                </Upload>
              </ImgCrop>
            </Form.Item>
          </Form.Item>
          <Form.Item
            label={
              <span>
                <span style={{ color: 'red' }}>*</span>排序
              </span>
            }
          >
            <Form.Item
              noStyle
              name="recommendOrder"
              rules={[{ required: true, message: '请输入' }]}
              initialValue={0}
            >
              <InputNumber placeholder="请输入" min={0} />
            </Form.Item>
            <div style={{ color: '#ccc' }}>数值越大，推荐顺序越前</div>
          </Form.Item>
          <Form.Item
            label="启用状态"
            name="status"
            rules={[{ required: true, message: '请选择!' }]}
            valuePropName="checked"
            initialValue={true}
          >
            <Switch disabled={this.state.shopStatus == 2} checkedChildren="开启" unCheckedChildren="关闭" />
          </Form.Item>
          <Form.Item
            label={<span>商家介绍</span>}
            rules={[{ required: true, message: '请输入!' }]}
          >
            <div
              style={{ position: 'relative', marginTop: '-15px', width: 600 }}
            >
              <Upload
                showUploadList={false}
                accept={'image/*'}
                // headers={{
                //   Authorization: getToken()
                //
                {...uploadProps(1)}
              >
                <div className="zxc" />
              </Upload>
              <CKEditor
                ref={(ckeditor) => {
                  this.ckeditor = ckeditor;
                }}
                value={this.state.content}
                config={{
                  toolbar: [
                    {
                      name: 'clipboard',
                      items: [
                        'Cut',
                        'Copy',
                        'Paste',
                        'PasteText',
                        'PasteFromWord',
                        '-',
                      ],
                    },
                    {
                      name: 'basicstyles',
                      items: [
                        'Bold',
                        'Italic',
                        'Underline',
                        '-',
                        'CopyFormatting',
                      ],
                    },
                    {
                      name: 'paragraph',
                      items: [
                        'NumberedList',
                        'BulletedList',
                        '-',
                        'Outdent',
                        'Indent',
                        '-',
                        'JustifyLeft',
                        'JustifyCenter',
                        'JustifyRight',
                        'JustifyBlock',
                        '-',
                      ],
                    },
                    { name: 'links', items: ['Link', 'Unlink'] },
                    { name: 'insert', items: ['Image', 'Table'] },
                    { name: 'styles', items: ['Font', 'FontSize'] },
                    { name: 'colors', items: ['TextColor', 'BGColor'] },
                    { name: 'tools', items: ['Maximize'] },
                  ],
                  extraPlugins: 'placeholder',
                  height: 250,
                  uploadUrl: '/home/media/upload',
                  removeDialogTabs: 'image:advanced;link:advanced',
                }}
                onChange={this.updateContent}
              />
            </div>
          </Form.Item>
          <Form.Item>
            {(type == 'add' || type == 'edit') && (
              <>
                <Button
                  onClick={() => {
                    history.back();
                  }}
                >
                  取消
                </Button>
                <Button className="mL15" type="primary" htmlType="submit">
                  保存
                </Button>
              </>
            )}
            {type == 'review' && (
              <Button className="mL15" type="primary" onClick={this.showReview}>
                审核
              </Button>
            )}
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
                isAdd={this.props.type == 'add'}
                onLocationChange={(locationInfo) => {
                  console.log('收到地图数据:', locationInfo);
                  const locationStr =
                    locationInfo &&
                    locationInfo.location.lat &&
                    locationInfo.location.lng
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
        <Modal
          style={{ minWidth: '50%' }}
          open={this.state.addGroupModalVisible}
          onCancel={() =>
            this.setState({
              addGroupModalVisible: false,
              majorList: this.state.majorList.filter((i) => i.id),
            })
          }
          title="新增行业类别"
          zIndex={2000}
          onOk={() => this.setState({ addGroupModalVisible: false })}
        >
          <Alert
            message="按住鼠标拖拽可调整展示顺序"
            showIcon
            style={{ textAlign: 'left' }}
          />
          <div className="modal-wrapper">
            <DndProvider backend={HTML5Backend}>
              <Table
                defaultSize="large"
                style={{ paddingTop: 25 }}
                columns={typeColumns}
                dataSource={this.state.majorList}
                search={false}
                options={false}
                pagination={false}
                components={this.components}
                scroll={{ y: 630 }}
                onRow={(record, index) => ({
                  index,
                  moveRow: this.moveRow,
                })}
              />
            </DndProvider>
            <Button
              onClick={() => this.addGroup('majorList')}
              type="dashed"
              style={{ marginTop: 20, width: '100%' }}
            >
              + 添加行业类别
            </Button>
          </div>
        </Modal>
        <Modal
          visible={this.state.reviewVisible}
          title="审核"
          onOk={this.onFinish}
          onCancel={() => {
            this.formRefs.current.resetFields();
            this.setState({
              searchType: 1,
              reviewVisible: false,
            });
          }}
        >
          <Form
            ref={this.formRefs}
            labelCol={{ span: 6 }}
            wrapperCol={{ span: 18 }}
            initialValues={{
              remember: true,
            }}
            autoComplete="off"
          >
            <Form.Item
              label="审核结果"
              name="searchType"
              rules={[{ required: true, message: '请输入！' }]}
              initialValue={1}
            >
              <Radio.Group
                onChange={(e) => {
                  this.setState({
                    searchType: e.target.value,
                  });
                }}
              >
                <Radio value={1}>通过</Radio>
                <Radio value={2}>驳回</Radio>
              </Radio.Group>
            </Form.Item>
            {this.state.searchType == 2 && (
              <Form.Item
                label="审核意见"
                name="keyword"
                rules={[{ required: true, message: '请输入！' }]}
              >
                <TextArea placeholder="请输入"></TextArea>
              </Form.Item>
            )}
          </Form>
        </Modal>
      </Spin>
    );
  }
}
export default AddShopMsgModal;
