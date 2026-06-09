import React from 'react';
import { PlusOutlined, CheckCircleTwoTone } from '@ant-design/icons';
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
  Upload,
  Modal,
  Radio,
  Switch,
  TimePicker,
  Alert,
  InputNumber,
  Table,
} from 'antd';
import { history, connect, Link } from 'umi';
import dayjs from 'dayjs';
import { urlName } from '@/utils/utils';
import ImgCrop from 'antd-img-crop';
const { Option } = Select;

class contractPhotos extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    isShouKuan: false,
    thumbnailFileList: [],
  };

  componentDidMount() {
    const { type, id, disabled = false } = this.props;
    this.setState(
      {
        type,
        id,
        disabled,
      },
      () => {
        type != 'add' && this.getData();
      },
    );
  }

  onFinish = () => {
    // 判断是否填写了图片
    if (!this.state.thumbnailFileList.length) {
      message.info('请上传图片！');
      return;
    }
    
    const urlList = this.state.thumbnailFileList.map((item) => item.response.data.url);
    console.log(urlList);

    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchId: this.state.id,
        searchStrField1: urlList.join(','),
      },
      url: `/ddql/business/shop/contract`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code == 10000) {
          message.success(res.msg);
          this.getData();
        } else {
          message.error(res.msg);
        }
      }
    })
  };

  getData = () => {
    this.setState({
      spinning: true,
    });
    this.props.dispatch({
      type: 'myModel/getSetData',
      payload: {
        searchId: this.state.id,
      },
      url: `/ddql/business/shop/selectById`,
      method: 'POST',
      myData: (res) => {
        this.setState({
          spinning: false,
        });
        if (res && res.code == 10000) {
          const data = res.data.shop
          let thumbnailFileList = data.contract ? data.contract.split(',') : [];
          thumbnailFileList = thumbnailFileList ? thumbnailFileList.map((item, index) => ({
            uid: String(index + 1),
            name: `image${index}.png`,
            status: 'done',
            url: item,
            response: { data: { url: item } },
          })) : [];

          this.setState({
            thumbnailFileList,
          })
        } else {
          message.error(res.msg);
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

  render() {
    const { thumbnailFileList, previewOpen, previewImage, type, disabled } = this.state;

    const props = {
      grid: false,
      width: 690,
      height: 312,
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
      <Spin spinning={this.state.spinning}>
        <Row align="middle" justify="space-between">
          <Col span={4}>
            <h2>合同照片</h2>
          </Col>
          <Col span={2}>
            <Button onClick={() => history.goBack()}>返回</Button>
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
          onFinish={this.onFinish}
          initialValues={{
            remember: true,
          }}
          autoComplete="off"
        >
          <Form.Item
            label={
              <span>
                <span style={{ color: 'red' }}>*</span>合同照片
              </span>
            }
          >
            <Form.Item noStyle>
              <ImgCrop {...props}>
                <Upload
                  disabled={disabled}
                  action="/ddql/file/upload"
                  listType="picture-card"
                  fileList={thumbnailFileList}
                  onChange={this.handleUploadChange('thumbnailFileList')}
                  onPreview={this.handlePreview}
                  beforeUpload={beforeUpload}
                  accept="image/jpeg,image/png"
                  headers={{ token: localStorage.getItem('token') }}
                >
                  {thumbnailFileList.length < 5 && uploadButton}
                </Upload>
              </ImgCrop>
            </Form.Item>
            <span style={{ color: '#ccc' }}>支持在线裁剪尺寸，图片格式支持jpg/jpeg/png</span>
          </Form.Item>
          <Form.Item>
            {(type != 'info') && (
              <>
                <Button
                  onClick={() => {
                    type == 'edit' ? this.getData() : history.goBack();
                  }}
                >
                  取消
                </Button>
                <Button className="mL15" type="primary" htmlType="submit">
                  保存
                </Button>
              </>
            )}
          </Form.Item>
        </Form>
        {/* 预览模态框 */}
        <Modal open={previewOpen} footer={null} onCancel={this.handleCancelPreview}>
          <img alt="预览" style={{ width: '100%' }} src={previewImage} />
        </Modal>
      </Spin>
    );
  }
}

export default connect()(contractPhotos);
