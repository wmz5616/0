import { post } from '@/utils/request';
import { urlName } from '@/utils/utils';
import { PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import {
  Button,
  Col,
  Form,
  Input,
  message,
  Modal,
  Popconfirm,
  Radio,
  Row,
  Spin,
  Tooltip,
  Upload,
} from 'antd';
import ImgCrop from 'antd-img-crop';
import React from 'react';
import { history } from 'umi';
const { TextArea } = Input;

class QualificationCertification extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    thumbnailFileList: [],
    carouselFileList: [],
    certResult: 3,
    reviewVisible: false,
    searchType: 1,
    searchId: undefined,
  };

  componentDidMount() {
    const { type, id, disabled = false, auditId } = this.props;
    this.setState(
      {
        type,
        id,
        disabled,
        auditId,
      },
      () => {
        type != 'add' && this.getData();
      },
    );
  }

  onFinish = () => {
    this.formRef.current.validateFields().then(async (values) => {
      const res = await post(
        this.state.searchId
          ? '/guzhe/shop/qualification/update'
          : `/guzhe/shop/qualification/add`,
        {
          businessLicense: this.state.thumbnailFileList.map(
            (cz) => cz.response.data.url,
          )[0],
          otherFile: this.state.carouselFileList
            .map((cz) => cz.response.data.url)
            .join(','),
          ...values,
          [this.state.type == 'review' ? 'shopAuditId' : 'shopId']: this.state.type == 'review' ? this.state.auditId : this.state.id,
          id: this.state.searchId || undefined,
          email: values.email || undefined,
        },
      );
      if (res && res.code == 10000) {
        message.success(res.msg);
        this.getData();
      } else {
        message.error(res?.msg);
      }
    });
  };

  getData = async () => {
    this.setState({
      spinning: true,
    });

    const res = await post(this.state.type == 'edit' ? `/guzhe/shop/qualification/get` : '/guzhe/shop/audit/get', {
      [this.state.type == 'edit' ? 'searchField1' : 'searchId']: this.state.id,
    });

    this.setState({
      spinning: false,
    });
    if (res && res.code == 10000) {
      const data = res.data || {};
      console.log('data', data);
      this.formRef.current.setFieldsValue({
        phone: data.phone,
        email: data.email,
      });
      this.setState(
        {
          certResult: data.certResult,
          thumbnailFileList: data?.businessLicense ? [
            {
              uid: '1',
              name: 'image.png',
              status: 'done',
              url: data.businessLicense,
              response: { data: { url: data.businessLicense } },
            },
          ] : [],
          carouselFileList: data.otherFile ? data.otherFile?.split(',').map((url, index) => ({
            uid: String(index + 1),
            name: 'image.png',
            status: 'done',
            url,
            response: { data: { url } },
          })) : [],
          searchId: data.id,
          rejectReason: data.rejectReason,
        });
    } else {
      message.error(res?.msg);
    }
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

  review = () => {
    this.formRefs.current.validateFields().then(async (values) => {
      const res = await post(this.state.type == 'edit' ? '/guzhe/shop/qualification/audit' : '/guzhe/shop/audit/qualification', {
        searchId: this.state.searchId,
        searchType: values.searchType,
        keyword: values.keyword,
      });
      if (res && res.code == 10000) {
        this.setState({
          reviewVisible: false,
        });
        message.success(res.msg);
        this.getData();
      } else {
        message.error(res?.msg);
      }
    });
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

  render() {
    const { certResult, thumbnailFileList, carouselFileList, type, disabled } =
      this.state;
    const props = {
      rid: false,
      aspect: this.state.cropAspect,
      resize: true,
      resizeAndDrag: true,
      modalTitle: '上传图片',
      modalWidth: 600,
      beforeCrop: this.handleBeforeCrop,
    };
    const uploadButton = (
      <button style={{ border: 0, background: 'none' }} type="button">
        <PlusOutlined />
        <div style={{ marginTop: 8 }}>上传</div>
      </button>
    );
    // 图片格式校验（限制为jpg/jpeg/png）
    const beforeUpload = (file) => {
      const isJpgOrPng =
        file.type == 'image/jpeg' || file.type == 'image/png';
      if (!isJpgOrPng) {
        message.error('仅支持上传 JPG/PNG 格式的图片！');
        return false;
      }
      return true;
    };
    return (
      <Spin spinning={this.state.spinning}>
        <Row align="middle" justify="space-between">
          <Col
            span={4}
            style={{ display: 'flex', alignItems: 'center', gap: 20 }}
          >
            <h2>资质认证</h2>
            <Tooltip
              placement="top"
              title={
                certResult == 2 ? (
                  <div>审核驳回的原因：{this.state.rejectReason}</div>
                ) : null
              }
            >
              <div
                style={{
                  color:
                    certResult == 0
                      ? '#f7a987'
                      : certResult == 1
                        ? '#35bb43'
                        : certResult == 2
                          ? '#df273e'
                          : '#2e99ff',
                  fontSize: 16,
                  display: 'flex',
                  gap: 5,
                  alignItems: 'center',
                }}
              >
                {certResult == 0
                  ? '待审核'
                  : certResult == 1
                    ? '已通过'
                    : certResult == 2
                      ? '已驳回'
                      : '未认证'}
                {certResult == 2 && <QuestionCircleOutlined />}
              </div>
            </Tooltip>
          </Col>
          <Col span={2}>
            <Button onClick={() => history.back()}>返回</Button>
          </Col>
        </Row>
        <Form
          ref={this.formRef}
          layout="vertical"
          labelCol={{
            span: 2,
          }}
          wrapperCol={{
            span: 15,
          }}
          initialValues={{
            remember: true,
          }}
          autoComplete="off"
          disabled={disabled}
        >
          {/* 营业执照 */}
          <Form.Item
            label={
              <span>
                <span style={{ color: 'red' }}>*</span>营业执照
              </span>
            }
          >
            <Form.Item noStyle>
              <span style={{ color: '#ccc', marginBottom: 10 }}>
                图片要求：大小不超过2M,分辨率不低于720*1280，必须为最新的纸质证件原件拍照或彩色扫描件，若未使用最新证件照，将无法通过备案审核；须证件四周圆角及卡正边缘清晰：若添加水印，须添加在证件空白位置不遮挡文字、图像信息，水印内容符合资质认证且不添加有效期。
              </span>
              <ImgCrop {...props}>
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
          </Form.Item>

          {/* 其他附件 */}
          <Form.Item
            label='其他附件'
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
                  {carouselFileList.length < 6 && uploadButton}
                </Upload>
              </ImgCrop>
            </Form.Item>
            <span style={{ color: '#ccc' }}>
              支持上传最多六张图，支持在线裁剪尺寸，图片格式支持jpg/jpeg/png
            </span>
          </Form.Item>
          <Form.Item
            label={
              <span>
                <span style={{ color: 'red' }}>*</span>联系电话
              </span>
            }
          >
            <Form.Item
              noStyle
              name="phone"
              rules={[{ required: true, message: '请输入！' }]}
            >
              <Input placeholder="请输入" />
            </Form.Item>
            <span style={{ color: '#ccc' }}>
              认证过程会致电该电话，认证期间请保诗电话畅通。
            </span>
          </Form.Item>
          <Form.Item
            label="邮箱"
            name="email"
            rules={[{ required: false, message: '请输入！' }]}
          >
            <Input placeholder="请输入" />
          </Form.Item>
          <Form.Item>
            {type != 'info' && (
              <>
                <Button
                  onClick={() => {
                    this.state.edit ? this.getData() : history.back();
                  }}
                >
                  取消
                </Button>
                {type != 'review' && (
                  <Popconfirm
                    placement="bottom"
                    title={
                      <>
                        <div>确认提交</div>
                        <div>
                          <text style={{ color: 'red' }}>
                            提交之后自动进行资质审核
                          </text>
                          <text style={{ color: '#999999' }}>
                            ，确定提交吗？
                          </text>
                        </div>
                      </>
                    }
                    onConfirm={this.onFinish}
                    okText="是"
                    cancelText="否"
                  >
                    <Button className="mL15" type="primary">
                      提交
                    </Button>
                  </Popconfirm>
                )}
                {this.state.certResult == 0 && (
                  <Button
                    className="mL15"
                    type="primary"
                    onClick={() => this.setState({ reviewVisible: true })}
                  >
                    审核
                  </Button>
                )}
              </>
            )}
          </Form.Item>
        </Form>
        <Modal
          visible={this.state.reviewVisible}
          title="人工审核"
          onOk={this.review}
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
                label="驳回原因"
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

export default QualificationCertification;
