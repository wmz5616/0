import React from 'react';
import {
  Modal,
  Form,
  Input,
  Select,
  Radio,
  DatePicker,
  Upload,
  message,
  InputNumber,
  Alert,
  Table,
  Button,
} from 'antd';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { history, connect, Link } from 'umi';
import moment from 'moment';
import CKEditor from 'react-ckeditor-wrapper';
import { DndProvider, DragSource, DropTarget } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import update from 'immutability-helper';
import ImgCrop from 'antd-img-crop';
const { Option } = Select;
const { RangePicker } = DatePicker;
import { urlName } from '@/utils/utils';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';
dayjs.extend(customParseFormat);
const layout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 },
};

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
class App extends React.Component {
  formRef = React.createRef();
  state = {
    pois: [],
    loading: false,
    xxxx: true,
    scheduling: 1,
    majorList: [],
    gymList: [],
    carouselFileList: [],
    thumbnailFileList: [],
  };

  components = {
    body: {
      row: DragableBodyRow,
    },
  };

  componentDidMount() {
    if (!this.props.add) {
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          searchId: this.props.id,
        },
        url: `/ddql/product/selectById`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            const values = res.data;
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
            this.formRef.current.setFieldsValue({
              coverImage: this.state.thumbnailFileList.map((cz) => cz.response.data.url)[0],
              galleryImages: this.state.carouselFileList.map((cz) => cz.response.data.url),
              status: values.status,
              remark: values.remark,
              name: values.name,
              detail: this.state.content,
              remark: values.remark,
              checkAdminIds: values.checkAdminIds,
              isVirtual: values.isVirtual,
              categoryIds: values.categoryList.map((c) => c.id),
              sort: values.sort,
              exchangeAmount: values.exchangeAmount,
              specification: values.specification,
              unit: values.unit,
              payWay: values.payWay,
              payAmount: values.payAmount ? values.payAmount / 100 : undefined,
              timeLimit: values.timeLimit ? values.timeLimit : undefined,
              scheduledTime:
                values.status == 3 ? dayjs(values.scheduledTime, 'YYYY-MM-DD HH:mm:ss') : undefined,
            });
            this.setState({
              isVirtual: values.isVirtual,
              content: values.description,
              id: values.id,
              location: values.location,
              thumbnailFileList: [
                {
                  uid: '1',
                  name: 'image.png',
                  status: 'done',
                  url: values.coverImage,
                  response: { data: { url: values.coverImage } },
                },
              ],
              carouselFileList: carouselFileList,
              content: values.detail,
              payWay: values.payWay,
              isLog: values.status,
            });
          } else {
            message.error(res.message);
          }
        },
      });
    }
    this.getGymList();
  }

  getGymList = () => {
    //获取商品专业列表
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {},
      url: `/ddql/product/category/lists`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          this.setState({
            majorList: res.data,
          });
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  addGroup = () => {
    const data = JSON.parse(JSON.stringify(this.state.majorList));
    data.push({ sort: data.length + 1 });
    this.setState({ majorList: data });
  };

  handleOk = () => {
    this.formRef.current.validateFields().then((values) => {
      console.log(values);
      const params = {
        coverImage: this.state.thumbnailFileList.map((cz) => cz.response.data.url)[0],
        galleryImages: this.state.carouselFileList.map((cz) => cz.response.data.url),
        status: values.status,
        remark: values.remark,
        name: values.name,
        detail: this.state.content,
        remark: values.remark,
        timeLimit: values.timeLimit,
        checkAdminIds: values.checkAdminIds,
        isVirtual: values.isVirtual,
        categoryIds: values.categoryIds.join(','),
        sort: values.sort,
        exchangeAmount: values.exchangeAmount,
        specification: values.specification,
        unit: values.unit,
        scheduledTime:
          values.status == 3 ? values.scheduledTime.format('YYYY-MM-DD HH:mm:ss') : undefined,
        payWay: this.state.payWay,
        payAmount: values.payAmount ? values.payAmount * 100 : undefined,
      };
      if (!this.props.add) {
        params.id = this.state.id;
      }
      this.props.dispatch({
        type: 'myModel/getSetData',
        payload: {
          ...params,
        },
        url: `/ddql/product/save`,
        method: 'POST',
        myData: (res) => {
          if (res && res.code === 10000) {
            message.success(res.msg);
            this.props.getData();
            this.props.handleOk();
          } else {
            message.error(res.msg);
            // this.setState({ isSelectForm: true });
          }
        },
      });
    });
  };

  handleCancel = () => {
    const { handleOk } = this.props;
    handleOk();
  };

  onChange = (value) => {
    this.setState({
      scheduling: value,
    });
  };

  beforeUpload = (file) => {
    const isJpgOrPng = file.name.indexOf('jfif') == -1;
    console.log(isJpgOrPng);
    if (!isJpgOrPng) {
      message.error('不符合类型文件');
    }

    return isJpgOrPng && true;
  };

  getBase64 = (img, callback) => {
    const reader = new FileReader();
    reader.addEventListener('load', () => callback(reader.result));
    reader.readAsDataURL(img);
  };

  updateContent = (value, index) => {
    this.setState({
      content: value,
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

  addCategory = () => {
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        categoryList: this.state.majorList,
      },
      url: `/ddql/product/category/add`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          this.setState({ addGroupModalVisible: false });
          this.getGymList();
        } else {
          message.error(res.msg);
          // this.setState({ isSelectForm: true });
        }
      },
    });
  };

  moveRow = (dragIndex, hoverIndex) => {
    console.log(dragIndex, hoverIndex);
    const { majorList } = this.state;
    const dragRow = majorList[dragIndex];
    console.log(hoverIndex);

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
        this.props.dispatch({
          type: 'myModel/getSetData',
          payload: {
            searchIds: this.state.majorList.map((xz) => xz.id),
          },
          url: `/ddql/product/category/sort`,
          method: 'POST',
          myData: (res) => {
            if (res && res.code === 10000) {
              message.success(res.msg);
              this.getGymList();
            } else {
              message.error(res.msg);
              // this.setState({ isSelectForm: true });
            }
          },
        });
      },
    );
  };

  render() {
    const typeColumns = [
      {
        title: '序号',
        render: (res, record, index) => <div>{index + 1}</div>,
      },
      {
        title: '分类名称',
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
              value={record.name}
              placeholder="请输入"
            />
          </div>
        ),
      },
      {
        title: '操作',
        render: (res, record, index) => (
          <div>
            {/* <span
              className="clickFont"
              onClick={() => {
                const params = {
                  name: record.name,
                  sort: record.sort,
                };
                if (record.id) {
                  params.id = record.id;
                }
                this.props.dispatch({
                  type: 'myModel/getSetData',
                  payload: {
                    ...params,
                  },
                  url: record.id ? `/api/admin/course/major/update` : `/api/admin/course/major/add`,
                  method: 'POST',
                  myData: (res) => {
                    if (res && res.code === 200) {
                      message.success(res.message);
                      this.getGymList();
                    } else {
                      message.error(res.message);
                      // this.setState({ isSelectForm: true });
                    }
                  },
                });
              }}
            >
              保存
            </span> */}
            <span
              className="mL15 red"
              onClick={() => {
                if (record.id) {
                  this.props.dispatch({
                    type: 'myModel/getSetData',
                    payload: {
                      deleteId: record.id,
                    },
                    url: `/ddql/product/category/delete`,
                    method: 'POST',
                    myData: (res) => {
                      if (res && res.code === 10000) {
                        message.success(res.msg);
                        this.getGymList();
                      } else {
                        message.error(res.msg);
                        // this.setState({ isSelectForm: true });
                      }
                    },
                  });
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
            >
              删除
            </span>
          </div>
        ),
      },
    ];
    const { add } = this.props;

    const { imageUrl, loading } = this.state;

    const uploadButton = (
      <div>
        {loading ? <LoadingOutlined /> : <PlusOutlined />}
        <div style={{ marginTop: 8 }}>上传</div>
      </div>
    );

    const uploadProps = (type, index) => {
      return {
        name: 'file',
        action: '/ddql/file/upload',
        headers: {
          token: localStorage.getItem('token'),
        },
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
            const url = info.file.response.data.url;
            const { ckeditor } = this;
            const ele = ckeditor.instance.document.createElement('img');
            ele.setAttribute('src', urlName + url);

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
    }; // 图片格式校验（限制为jpg/jpeg/png）
    const beforeUpload = (file) => {
      const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
      if (!isJpgOrPng) {
        message.error('仅支持上传 JPG/PNG 格式的图片！');
        return false;
      }
      return true;
    };
    return (
      <>
        <Modal
          maskClosable={false}
          title={add ? '新增商品' : '编辑商品'}
          visible
          onOk={this.handleOk}
          onCancel={this.handleCancel}
          width={800}
        >
          <Form ref={this.formRef} {...layout}>
            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>商品封面图
                </span>
              }
            >
              <Form.Item noStyle>
                <ImgCrop {...props}>
                  <Upload
                    action="/ddql/file/upload"
                    listType="picture-card"
                    fileList={this.state.thumbnailFileList}
                    onChange={this.handleUploadChange('thumbnailFileList')}
                    onPreview={this.handlePreview}
                    beforeUpload={beforeUpload}
                    accept="image/jpeg,image/png"
                    headers={{ token: localStorage.getItem('token') }}
                  >
                    {this.state.thumbnailFileList.length < 1 && uploadButton}
                  </Upload>
                </ImgCrop>
              </Form.Item>
              <span style={{ color: '#ccc' }}>支持在线裁剪尺寸，图片格式支持jpg/jpeg/png</span>
            </Form.Item>
            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>商品轮播图
                </span>
              }
            >
              <Form.Item noStyle>
                <ImgCrop {...props}>
                  <Upload
                    action="/ddql/file/upload"
                    listType="picture-card"
                    fileList={this.state.carouselFileList}
                    onChange={this.handleUploadChange('carouselFileList')}
                    onPreview={this.handlePreview}
                    beforeUpload={beforeUpload}
                    accept="image/jpeg,image/png"
                    headers={{ token: localStorage.getItem('token') }}
                  >
                    {this.state.carouselFileList.length < 5 && uploadButton}
                  </Upload>
                </ImgCrop>
              </Form.Item>
              <span style={{ color: '#ccc' }}>支持在线裁剪尺寸，图片格式支持jpg/jpeg/png</span>
            </Form.Item>
            <Form.Item
              label="商品名称"
              name="name"
              rules={[{ required: true, message: '请输入!' }]}
            >
              <Input placeholder="请输入" />
            </Form.Item>
            <Form.Item label="规格" name="specification" rules={[{ required: false }]}>
              <Input placeholder="请输入" />
            </Form.Item>
            <Form.Item label="单位" name="unit" rules={[{ required: true, message: '请输入!' }]}>
              <Input placeholder="请输入" />
            </Form.Item>
            <Form.Item
              label="支付方式"
              name="payWay"
              rules={[{ required: true, message: '请输入!' }]}
            >
              <Select
                showSearch
                placeholder="请选择"
                onChange={(e) => {
                  this.setState({
                    payWay: e,
                  });
                }}
              >
                <Option value={0}>金币支付</Option>
                <Option value={1}>组合支付</Option>
                <Option value={2}>现金支付</Option>
              </Select>
            </Form.Item>
            {this.state.payWay == 0 ? (
              <Form.Item label="兑换币额" name="exchangeAmount" rules={[{ required: true }]}>
                <InputNumber min={0} placeholder='请输入'/>
              </Form.Item>
            ) : this.state.payWay == 2 ? (
              <Form.Item label="支付金额(元)" name="payAmount" rules={[{ required: true }]}>
                <InputNumber min={0} placeholder="请输入"/>
              </Form.Item>
            ) : this.state.payWay == 1 ? (
              <Form.Item>
                <div
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: 8,
                    paddingLeft: 81,
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <span style={{ flexShrink: 0 }}>
                      <span style={{ color: 'red' }}>*</span>兑换币额：
                    </span>
                    <Form.Item
                      noStyle
                      name="exchangeAmount"
                      rules={[{ required: true, message: '请输入兑换币额' }]}
                    >
                      <InputNumber min={0} placeholder="请输入" style={{ width: '100%' }} />
                    </Form.Item>
                  </div>

                  <span>+</span>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <span style={{ flexShrink: 0 }}>
                      <span style={{ color: 'red' }}>*</span>支付金额(元)：
                    </span>
                    <Form.Item
                      noStyle
                      name="payAmount"
                      rules={[{ required: true, message: '请输入支付金额' }]}
                    >
                      <InputNumber min={0} placeholder="请输入" style={{ width: '100%' }} />
                    </Form.Item>
                  </div>
                </div>
              </Form.Item>
            ) : (
              ''
            )}
            <Form.Item
              label={
                <div>
                  <span style={{ color: 'red' }}>*</span>商品分类
                </div>
              }
              rules={[{ required: true, message: '请选择!' }]}
            >
              <div style={{ display: 'flex', width: '100%' }}>
                <Form.Item
                  name="categoryIds"
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
            <Form.Item initialValue={0} label="排序" name="sort" rules={[{ required: false }]}>
              <InputNumber precision={0} />
            </Form.Item>
            <div style={{ position: 'relative' }}>
              <Form.Item
                label={
                  <div>
                    <span style={{ color: 'red' }}>*</span>上架状态
                  </div>
                }
              >
                <Form.Item noStyle name="status" rules={[{ required: true, message: '请选择上架状态!' }]}>
                  <Radio.Group onChange={(e) => this.setState({ isLog: e.target.value })}>
                    <Radio value={1}>上架</Radio>
                    <Radio value={2}>下架</Radio>
                    <Radio value={3}>定时上架</Radio>
                  </Radio.Group>
                </Form.Item>
                {this.state.isLog == 3 && (
                  <div
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      position: 'absolute',
                      bottom: 0,
                      left: 220,
                    }}
                  >
                    <Form.Item noStyle name="scheduledTime" showTime rules={[{ required: true }]}>
                      <DatePicker showTime />
                    </Form.Item>
                  </div>
                )}
              </Form.Item>
            </div>
            <Form.Item label="是否是虚拟商品" name="isVirtual" rules={[{ required: true }]}>
              <Radio.Group
                disabled={!this.props.add}
                onChange={(e) =>
                  this.setState({
                    isVirtual: e.target.value,
                  })
                }
              >
                <Radio value={1}>是</Radio>
                <Radio value={0}>否</Radio>
              </Radio.Group>
            </Form.Item>
            {this.state.isVirtual == 1 && (
              <Form.Item label="有效期（天）" name="timeLimit" rules={[{ required: true }]}>
                <InputNumber precision={0} />
              </Form.Item>
            )}
            {this.state.isVirtual == 1 && (
              <Form.Item label="核销人员" name="checkAdminIds" rules={[{ required: true }]}>
                <Select mode="multiple" placeholder="请选择">
                  {this.props.adminList.map((xz) => (
                    <Option value={xz.id}>
                      {xz.nickname}（{xz.phone}）
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            )}
            <Form.Item
              label={
                <span>
                  <span style={{ color: 'red' }}>*</span>商品详情
                </span>
              }
              rules={[{ required: true, message: '请输入!' }]}
            >
              <div style={{ position: 'relative', marginTop: '-15px' }}>
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
                        items: ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-'],
                      },
                      {
                        name: 'basicstyles',
                        items: ['Bold', 'Italic', 'Underline', '-', 'CopyFormatting'],
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
            <Form.Item label="备注" name="remark" rules={[{ required: false }]}>
              <Input.TextArea rows={3} placeholder="请输入" />
            </Form.Item>
          </Form>
          <Modal
            style={{ minWidth: '50%' }}
            open={this.state.addGroupModalVisible}
            onCancel={() => this.setState({ addGroupModalVisible: false })}
            title="新增商品分类"
            zIndex={2000}
            onOk={this.addCategory}
          >
            <Alert message="按住鼠标拖拽可调整展示顺序" showIcon style={{ textAlign: 'left' }} />
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
                onClick={this.addGroup}
                type="dashed"
                style={{ marginTop: 20, width: '100%' }}
              >
                + 添加商品分类
              </Button>
            </div>
          </Modal>
        </Modal>
      </>
    );
  }
}
export default connect()(App);
