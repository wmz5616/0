import React from 'react';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
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
  Radio,
  DatePicker,
  Switch,
  Steps,
  Checkbox,
  Result,
  Alert,
} from 'antd';
import { PageContainer } from '@ant-design/pro-layout';
import { history, connect, Link } from 'umi';
import XX from './components/updateform';
import {
  DownOutlined,
  UpOutlined,
  CheckCircleTwoTone,
  CloseCircleOutlined,
  InfoCircleOutlined,
} from '@ant-design/icons';
// import { setToken } from '@/utils/authority';
const { Option } = Select;
const { RangePicker } = DatePicker;
import moment from 'moment';
import { urlName } from '@/utils/utils';
const layout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 10 },
};

class NoticeNotice extends React.Component {
  formRef = React.createRef();
  formRefs = React.createRef();
  state = {
    spinning: false,
    xxx: false,
    current: 0,
    licenceUrls: [],
    openAccountLicenceUrl: [],
    IdPhoto: [],
    IdPhotoB: [],
    licenceUrl: [],
    Disablesss: true,
    // operationTypes:'1'
  };

  componentDidMount() {
    const { stadium_id, detail } = this.props.location.query;
    const xx = JSON.parse(detail);
    this.setState({
      xx,
      operationTypes: xx.operationType,
      effectiveType: xx && xx.businessEndDate == '长期' ? 1 : 0,
      licenceUrl: xx.licenceUrlLocal
        ? [
            {
              uid: '-1',
              name: 'image.png',
              status: 'done',
              url: xx.licenceUrlLocal.startsWith('http') ? xx.licenceUrlLocal : urlName + xx.licenceUrlLocal,
            },
          ]
        : [],

      licenceUrls: xx.licenceUrlLocal
        ? [
            {
              uid: '-1',
              name: 'image.png',
              status: 'done',
              url: xx.licenceUrlLocal.startsWith('http') ? xx.licenceUrlLocal : urlName + xx.licenceUrlLocal,
            },
          ]
        : [],

      IdPhoto: xx.legalLicenceFrontUrlLocal
        ? [
            {
              uid: '-1',
              name: 'image.png',
              status: 'done',
              url: xx.legalLicenceFrontUrlLocal.startsWith('http') ? xx.legalLicenceFrontUrlLocal : urlName + xx.legalLicenceFrontUrlLocal,
            },
          ]
        : [], //身份证正面

      IdPhotoB: xx.legalLicenceBackUrlLocal
        ? [
            {
              uid: '-1',
              name: 'image.png',
              status: 'done',
              url: xx.legalLicenceBackUrlLocal.startsWith('http') ? xx.legalLicenceBackUrlLocal : urlName + xx.legalLicenceBackUrlLocal,
            },
          ]
        : [], //身份证反面

      documentType: xx.cardEndDate === '长期' ? 1 : 0,

      openAccountLicenceUrl: xx.openAccountLicenceUrlLocal
        ? [
            {
              uid: '-1',
              name: 'image.png',
              status: 'done',
              url: xx.openAccountLicenceUrlLocal.startsWith('http') ? xx.openAccountLicenceUrlLocal : urlName + xx.openAccountLicenceUrlLocal,
            },
          ]
        : [], //*开户许可证照片
    });
    this.setState({
      provincexx: xx.province ? [xx.province, xx.city] : undefined, //开户所属地区
      bankNamexx: xx && xx.bankName,
      bankNames: xx.bankName,
      bankBranchs: xx.bankBranch,
    });
    this.formRef.current.setFieldsValue({
      stadium_id: Number(stadium_id),
      operationType: xx.operationType,
      merchantName: xx.merchantName, //商户名称
      contactPhone: xx.contactPhone, //联系电话
      email: xx.email, //邮箱地址

      storeName: xx.storeName, //门店名称
      storeCounty: xx.storeProvince ? [xx.storeProvince, xx.storeCity, xx.storeCounty] : undefined, //门店所属地区
      storeAddr: xx.storeAddr, //详情地址

      businessName: xx && xx.businessName, //主体名称
      businessNo: xx && xx.businessNo,
      mainType: xx.mainType == 0 ? undefined : Number(xx && xx.mainType), //主体类型
      licenceUrl: xx.licenceUrl, //营业执照照片
      parentChannelMerchantNo: xx.parentChannelMerchantNo, //执照/证书编号
      legalPerson: xx && xx.legalPerson, //法人姓名

      businessProvince: [xx && xx.businessProvince, xx && xx.businessCity, xx && xx.businessCounty],
      businessAddr: xx && xx.businessAddr,
      effectiveType: xx && xx.businessEndDate == '长期' ? '1' : '0',
      businessBeginDate:
        xx &&
        (xx.businessEndDate == '长期'
          ? moment(xx.businessBeginDate, 'YYYY-MM-DD')
          : [moment(xx.businessBeginDate, 'YYYY-MM-DD'), moment(xx.businessEndDate, 'YYYY-MM-DD')]),

      documentType: xx.cardEndDate === '长期' ? '1' : '0',
      cardBeginDate: xx.cardEndDate
        ? xx.cardEndDate == '长期'
          ? moment(xx.cardBeginDate, 'YYYY-MM-DD')
          : [moment(xx.cardBeginDate, 'YYYY-MM-DD'), moment(xx.cardEndDate, 'YYYY-MM-DD')]
        : undefined,

      cardName: xx.cardName, //证件持有人姓名
      cardNo: xx.cardNo, //证件号码
      cardMobile: xx.cardMobile, //手机号码

      accType: xx.accType ? Number(xx.accType) : undefined, //账户类型（操作类型为1必填）：10070=对公账户，10071=法人账户
      accCardNo: xx.accCardNo, //银行账号
      accMobile: xx.accMobile, //银行开户预留手机号
      accName: xx.accName, //户名（法人或营业执照名称）

      bank: xx.bank,
      province: xx.province ? [xx.province, xx.city] : undefined,

      bankLinkNo: xx.bankLinkNo,
    });
  }

  nextss = (vv) => {
    this.setState(
      {
        Disablesss: false,
      },
      () => {
        this.formRef.current.validateFields().then((values) => {
          console.log(this.state);
          this.setState(
            {
              stadium_id: values.stadium_id, //场所id
              operationType: values.operationType,
              merchantBasic: {
                merchantName: values.merchantName, //商户名称
                contactPhone: values.contactPhone, //联系电话
                email: values.email, //邮箱地址
              },

              //负责人身份证信息
              merchantCard: {
                cardName: values.cardName, //身份证名称
                cardNo: values.cardNo, //身份证号码
                cardMobile: values.cardMobile, //手机
                cardBeginDate:
                  values.cardBeginDate &&
                  (values.cardBeginDate.length > 1
                    ? values.cardBeginDate[0].format('YYYY-MM-DD')
                    : values.cardBeginDate.format('YYYY-MM-DD')), //身份证开始时间
                cardEndDate:
                  values.cardBeginDate &&
                  (values.cardBeginDate.length > 1
                    ? values.cardBeginDate[1].format('YYYY-MM-DD')
                    : '长期'), //身份证结束时间
              },

              //门店信息
              merchanStore: {
                storeName: values.storeName, //门店名称
                storeProvince: values.storeCounty && values.storeCounty[0], //省
                storeCity: values.storeCounty && values.storeCounty[1], //市
                storeCounty: values.storeCounty && values.storeCounty[2], //区
                storeAddr: values.storeAddr, //详情地址
              },

              //营业信息（操作类型为1时必填）
              merchanBusiness:
                values.operationType == 1 || true
                  ? {
                      businessName: values.businessName, //主体名称
                      businessNo: values.businessNo, //主题编号
                      mainType: values.mainType, //主体类型
                      legalPerson: values.legalPerson, //法人名称
                      businessProvince: values.businessProvince ? values.businessProvince[0] : '', //营业地址省
                      businessCity: values.businessProvince ? values.businessProvince[1] : '', //营业地址市
                      businessCounty: values.businessProvince ? values.businessProvince[2] : '', //营业地址区
                      businessAddr: values.businessAddr, //营业详细地址
                      businessBeginDate: values.businessBeginDate
                        ? values.businessBeginDate.length > 1
                          ? values.businessBeginDate[0].format('YYYY-MM-DD')
                          : values.businessBeginDate.format('YYYY-MM-DD')
                        : '', //营业开始时间
                      businessEndDate: values.businessBeginDate
                        ? values.businessBeginDate.length > 1
                          ? values.businessBeginDate[1].format('YYYY-MM-DD')
                          : '长期'
                        : '', //营业结束时间
                    }
                  : undefined,
              //账户类型
              acc: {
                accType: values.accType, //账户类型10070=对公账户，10071=法人账户
                accCardNo: values.accCardNo, //银行账号
                accMobile: values.accMobile, //开户预留手机
                accName: values.accName, //账户名
                bank: values.bank, //开户行编号
                bankName: this.state.bankNames, //开户行名字
                province: values.province && values.province[0], //开户地址省
                city: values.province && values.province[1], //开户地址市
                bankLinkNo: values.bankLinkNo, //支行联行号
                bankBranch: this.state.bankBranchs, //支行名称
              },

              licenceUrl: this.state.licenceUrls
                ? this.state.licenceUrls.length > 0 && this.state.licenceUrls
                : undefined, //营业执照照片
              // parentChannelMerchantNo: values.parentChannelMerchantNo, //主商户编号

              legalLicenceFrontUrl:
                this.state.IdPhoto && this.state.IdPhoto.length > 0 && this.state.IdPhotoB, //身份证正面
              legalLicenceBackUrl:
                this.state.IdPhotoB && this.state.IdPhotoB.length > 0 && this.state.IdPhotoB, //身份证反面
              openAccountLicenceUrl:
                this.state.openAccountLicenceUrl &&
                this.state.openAccountLicenceUrl.length > 0 &&
                this.state.openAccountLicenceUrl, //*开户许可证照片
              // current: this.state.current + 1,
            },
            () => {
              this.setState(
                {
                  Disables: this.state.current == 1 ? true : false,
                },
                () => {
                  const { id } = this.props.location.query;
                  const params = {
                    id: id,
                    status: vv,
                    // stadium_id: this.state.stadium_id, //场所id
                    operationType: this.state.operationType,
                    ...this.state.merchantBasic,
                    ...this.state.merchantCard, //负责人信息
                    ...this.state.merchanStore, //门店信息

                    ...this.state.acc,

                    licenceUrlLocal: this.state.licenceUrls
                      ? this.state.licenceUrls.length > 0 && this.state.licenceUrls[0].url
                        ? this.state.licenceUrls[0].url
                        : ''
                      : '', //营业执照照片
                    // parentChannelMerchantNo: '10090719235', //执照/证书编号

                    legalLicenceFrontUrlLocal:
                      this.state.IdPhoto.length > 0 && this.state.IdPhoto[0].url
                        ? this.state.IdPhoto[0].url
                        : '', //身份证正面
                    legalLicenceBackUrlLocal:
                      this.state.IdPhotoB.length > 0 && this.state.IdPhotoB[0].url
                        ? this.state.IdPhotoB[0].url
                        : '', //身份证反面
                    openAccountLicenceUrlLocal:
                      this.state.openAccountLicenceUrl.length > 0 &&
                      this.state.openAccountLicenceUrl[0].url
                        ? this.state.openAccountLicenceUrl[0].url
                        : '', //*开户许可证照片
                  };
                  const merchanBusiness = this.state.merchanBusiness
                    ? this.state.merchanBusiness
                    : {};
                  const mergedObj = Object.assign({}, params, merchanBusiness);
                  this.props.dispatch({
                    type: 'myModel/getSetData',
                    payload: {
                      ...mergedObj,
                    },
                    url: `/ddql/merchant/update`,
                    method: 'POST',
                    myData: (res) => {
                      if (res && res.code === 10000) {
                        message.success(res.msg);
                        this.props.history.goBack();
                      } else {
                        this.setState({
                          Disablesss: true,
                        });
                        message.error(res.message);
                      }
                    },
                  });
                },
              );
            },
          );
        });
      },
    );
  };

  next = () => {
    this.formRef.current.validateFields().then((values) => {
      this.setState(
        {
          stadium_id: values.stadium_id, //场所id
          operationType: values.operationType,
          merchantBasic: {
            merchantName: values.merchantName, //商户名称
            contactPhone: values.contactPhone, //联系电话
            email: values.email, //邮箱地址
          },

          //负责人身份证信息
          merchantCard: {
            cardName: values.cardName, //身份证名称
            cardNo: values.cardNo, //身份证号码
            cardMobile: values.cardMobile, //手机
            cardBeginDate:
              values.cardBeginDate.length > 1
                ? values.cardBeginDate[0].format('YYYY-MM-DD')
                : values.cardBeginDate.format('YYYY-MM-DD'), //身份证开始时间
            cardEndDate:
              values.cardBeginDate.length > 1
                ? values.cardBeginDate[1].format('YYYY-MM-DD')
                : '长期', //身份证结束时间
          },

          //门店信息
          merchanStore: {
            storeName: values.storeName, //门店名称
            storeProvince: values.storeCounty[0], //省
            storeCity: values.storeCounty[1], //市
            storeCounty: values.storeCounty[2], //区
            storeAddr: values.storeAddr, //详情地址
          },

          //营业信息（操作类型为1时必填）
          merchanBusiness:
            values.operationType == 1 || true
              ? {
                  businessName: values.businessName, //主体名称
                  businessNo: values.businessNo, //主题编号
                  mainType: values.mainType, //主体类型
                  legalPerson: values.legalPerson, //法人名称
                  businessProvince: values.businessProvince ? values.businessProvince[0] : '', //营业地址省
                  businessCity: values.businessProvince ? values.businessProvince[1] : '', //营业地址市
                  businessCounty: values.businessProvince ? values.businessProvince[2] : '', //营业地址区
                  businessAddr: values.businessAddr, //营业详细地址
                  businessBeginDate: values.businessBeginDate
                    ? values.businessBeginDate.length > 1
                      ? values.businessBeginDate[0].format('YYYY-MM-DD')
                      : values.businessBeginDate.format('YYYY-MM-DD')
                    : '', //营业开始时间
                  businessEndDate: values.businessBeginDate
                    ? values.businessBeginDate.length > 1
                      ? values.businessBeginDate[1].format('YYYY-MM-DD')
                      : '长期'
                    : '', //营业结束时间
                }
              : undefined,
          //账户类型
          acc: {
            accType: values.accType, //账户类型10070=对公账户，10071=法人账户
            accCardNo: values.accCardNo, //银行账号
            accMobile: values.accMobile, //开户预留手机
            accName: values.accName, //账户名
            bank: values.bank, //开户行编号
            bankName: this.state.bankNames, //开户行名字
            province: values.province && values.province[0], //开户地址省
            city: values.province && values.province[1], //开户地址市
            bankLinkNo: values.bankLinkNo, //支行联行号
            bankBranch: this.state.bankBranchs, //支行名称
          },

          // licenceUrl: this.state.licenceUrl
          //   ? this.state.licenceUrl[0].url
          //   : this.state.licenceUrls
          //   ? this.state.licenceUrls.length > 0 && this.state.licenceUrls[0].url
          //   : undefined, //营业执照照片
          // parentChannelMerchantNo: values.parentChannelMerchantNo, //主商户编号

          legalLicenceFrontUrl:
            this.state.IdPhoto && this.state.IdPhoto.length > 0 && this.state.IdPhoto, //身份证正面
          legalLicenceBackUrl:
            this.state.IdPhotoB && this.state.IdPhotoB.length > 0 && this.state.IdPhotoB, //身份证反面
          openAccountLicenceUrl:
            this.state.openAccountLicenceUrl &&
            this.state.openAccountLicenceUrl.length > 0 &&
            this.state.openAccountLicenceUrl, //*开户许可证照片

          current: this.state.current + 1,
        },
        () => {
          this.setState({
            Disables: this.state.current == 1 ? true : false,
          });
        },
      );
    });
  };

  prev = () => {
    this.setState(
      {
        current: this.state.current - 1,
      },
      () => {
        this.setState({
          Disables: this.state.current == 1 ? true : false,
        });
      },
    );
  };

  submits = (vv) => {
    const { id } = this.props.location.query;
    console.log(this.state.licenceUrl);
    const params = {
      id: id,
      status: vv,
      // stadium_id: this.state.stadium_id, //场所id
      operationType: this.state.operationType,
      ...this.state.merchantBasic,
      ...this.state.merchantCard, //负责人信息
      ...this.state.merchanStore, //门店信息

      ...this.state.acc,

      licenceUrl: this.state.licenceUrls
        ? this.state.licenceUrls.length > 0 && this.state.licenceUrls[0].url
          ? this.state.licenceUrls[0].url
          : ''
        : '', //营业执照照片
      // parentChannelMerchantNo: '10090719235', //执照/证书编号

      legalLicenceFrontUrl:
        this.state.IdPhoto.length > 0 && this.state.IdPhoto[0].url ? this.state.IdPhoto[0].url : '', //身份证正面
      legalLicenceBackUrl:
        this.state.IdPhotoB.length > 0 && this.state.IdPhotoB[0].url
          ? this.state.IdPhotoB[0].url
          : '', //身份证反面
      openAccountLicenceUrl:
        this.state.openAccountLicenceUrl.length > 0 && this.state.openAccountLicenceUrl[0].url
          ? this.state.openAccountLicenceUrl[0].url
          : '', //*开户许可证照片
    };
    const merchanBusiness = this.state.merchanBusiness ? this.state.merchanBusiness : {};
    const mergedObj = Object.assign({}, params, merchanBusiness);
    this.props.dispatch({
      type: 'myModel/getSetData',

      payload: {
        ...mergedObj,
      },
      url: `/ddql/merchant/update`,
      method: 'POST',
      myData: (res) => {
        if (res && res.code === 10000) {
          message.success(res.msg);
          this.props.history.goBack();
          // this.setState(
          //   {
          //     current: this.state.current + 1,
          //   },
          //   () => {
          //     this.setState({
          //       dataDetails: res.data,
          //     });
          //   },
          // );
        } else {
          message.error(res.msg);
        }
      },
    });
  };

  contactPhones = (e) => {
    this.formRef.current.setFieldsValue({
      cardMobile: e.target.value,
      accMobile: e.target.value,
    });
  };

  fullName = (e) => {
    this.formRef.current.setFieldsValue({
      cardName: e.target.value,
    });
  };

  addressa = (e) => {
    this.formRef.current.setFieldsValue({
      businessAddr: e.target.value,
    });
  };

  areaa = (e) => {
    this.formRef.current.setFieldsValue({
      businessProvince: e,
    });
  };

  render() {
    const { current, stadiumList = [], dataDetails = {} } = this.state;

    const routes = [
      {
        path: '/',
        breadcrumbName: '首页',
      },
      {
        breadcrumbName: '',
      },
      {
        breadcrumbName: `新增商户`,
      },
    ];

    const steps = [
      {
        title: 'First',
        // content: (
        // ),
      },
      {
        title: 'Second',
        // content: (
        //   <div style={{ marginTop: 50 }}>
        //     <Form ref={this.formRefs} {...layout}>
        //       <XX />
        //     </Form>
        //   </div>
        // ),
      },
      {
        title: 'Last',
        // content: 'Last-content',
      },
    ];

    // const items = steps.map((item) => ({
    //   key: item.title,
    //   title: item.title,
    // }));

    return (
      <PageContainer
        header={{
          title: (
            <div style={{ display: 'flex', gap: 10 }}>
              编辑商户在线进件
              {this.state?.xx?.applicationStatus == 'REVIEW_BACK' && (
                <div style={{ color: 'red' }}>驳回原因：{this.state?.xx?.auditOpinion}</div>
              )}
            </div>
          ),
          breadcrumb: {
            itemRender: this.itemRender,
            routes,
          },
          extra: (
            <div>
              <Popconfirm
                title={
                  <>
                    <div>温馨提示</div>
                    <div>
                      <span style={{ color: '#ccc' }}>否要保存草稿</span>
                    </div>
                  </>
                }
                onConfirm={() => this.nextss(4)}
                okText="是"
                cancelText="否"
                onCancel={() => {
                  history.goBack();
                }}
              >
                <Button>返回</Button>
              </Popconfirm>
            </div>
          ),
        }}
      >
        <div style={{ backgroundColor: '#fff', padding: 24 }}>
          <Steps current={current}>
            <Steps.Step title="填写进件信息" />
            <Steps.Step title="确认进件信息" />
            <Steps.Step title="完成" />
          </Steps>

          {/* <Steps current={current} items={items} /> */}

          <div className="steps-content">{steps[current].content}</div>
          <div className="steps-action">
            {current == 0 || current == 1 ? (
              <Form ref={this.formRef} {...layout} style={{ marginTop: 50 }}>
                <XX
                  formRef={this.formRef}
                  IDImage={(e, ee, eee, eeee) => {
                    this.setState({
                      IdPhoto: e,
                      IdPhotoB: ee, //身份证反面照片
                      licenceUrl: eee, //营业执照照片
                      licenceUrls: eee, //营业执照照片
                      openAccountLicenceUrl: eeee, //开户许可证照片
                    });
                  }}
                  bankDetails={(e, ee) => {
                    this.setState({
                      bankNames: e,
                      bankBranchs: ee,
                    });
                  }}
                  Disablesss={this.state.Disablesss}
                  Disables={this.state.Disables}
                  contactPhones={(e) => this.contactPhones(e)}
                  fullName={(e) => this.fullName(e)}
                  addressa={(e) => this.addressa(e)}
                  areaa={(e) => this.areaa(e)}
                  operationTypes={this.state.operationTypes}
                  effectiveType={this.state.effectiveType}
                  // documentType={this.state.documentType}
                  // province={thisountOpeningAddress(11)}
                  provincexx={this.state.provincexx}
                  bankNamexx={this.state.bankNamexx}
                  IdPhotoB={this.state.IdPhotoB}
                  IdPhoto={this.state.IdPhoto}
                  licenceUrl={this.state.licenceUrl}
                  openAccountLicenceUrl={this.state.openAccountLicenceUrl}
                />
              </Form>
            ) : (
              ''
            )}

            {current == 0 && (
              <>
                <Row>
                  <Col span={5} offset={8}>
                    <Button
                      type="primary"
                      onClick={() => this.next()}
                      style={{ marginRight: 15, width: 120, height: 40, marginTop: 10 }}
                    >
                      下一步
                    </Button>
                    <Button
                      onClick={() => this.nextss(4)}
                      style={{ marginRight: 15, width: 120, height: 40, marginTop: 10 }}
                    >
                      保存草稿
                    </Button>
                  </Col>
                </Row>
              </>
            )}

            {current == 1 && (
              <>
                <Row>
                  <Col span={5} offset={8}>
                    <Button
                      type="primary"
                      onClick={() => this.submits(2)}
                      style={{ marginRight: 15, width: 120, height: 40, marginTop: 10 }}
                    >
                      提交
                    </Button>
                    <Button
                      style={{ marginRight: 15, width: 120, height: 40, marginTop: 10 }}
                      onClick={() => this.prev()}
                    >
                      上一步
                    </Button>
                  </Col>
                </Row>
              </>
            )}

            {current == 2 && (
              <>
                <Result
                  status={dataDetails.returnMsg !== '请求成功' ? 'error' : 'success'}
                  title={
                    dataDetails.returnMsg !== '请求成功'
                      ? dataDetails.returnMsg
                      : '进件申请提交成功，正在等待开通中'
                  }
                  subTitle={
                    dataDetails.returnMsg !== '请求成功'
                      ? dataDetails.returnMsg
                      : '预计一个工作日内有审批结果'
                  }
                />

                <div
                  style={{
                    backgroundColor: '#fafafa',
                    width: 600,
                    margin: '0 auto',
                    padding: '40px 20px',
                    fontSize: 14,
                  }}
                >
                  <div style={{ display: 'flex', marginBottom: 20 }}>
                    <div style={{ width: 140, textAlign: 'right', color: '#333' }}>商户名称：</div>
                    <div style={{ flex: 1, paddingLeft: 8, color: '#666' }}>{this.state && this.state.merchantName}</div>
                  </div>
                  <div style={{ display: 'flex', marginBottom: 20 }}>
                    <div style={{ width: 140, textAlign: 'right', color: '#333' }}>进件申请单编号：</div>
                    <div style={{ flex: 1, paddingLeft: 8, color: '#666' }}>
                      {dataDetails && dataDetails.applicationNo}
                      <a style={{ marginLeft: 15, color: '#1890ff' }} onClick={() => this.props.history.goBack()}>查看详情&gt;</a>
                    </div>
                  </div>
                  <div style={{ display: 'flex', marginBottom: 20 }}>
                    <div style={{ width: 140, textAlign: 'right', color: '#333' }}>提交时间：</div>
                    <div style={{ flex: 1, paddingLeft: 8, color: '#666' }}>{new Date().toLocaleString('zh-CN')}</div>
                  </div>
                  <div style={{ display: 'flex' }}>
                    <div style={{ width: 140, textAlign: 'right', color: '#333' }}>申请状态：</div>
                    <div style={{ flex: 1, paddingLeft: 8, color: '#666' }}>
                      {dataDetails && dataDetails.applicationStatus === 'REVIEWING' && (
                        <span>
                          <InfoCircleOutlined style={{ color: '#1890ff', marginRight: 5 }} /> 申请审核中
                        </span>
                      )}
                      {dataDetails && dataDetails.applicationStatus === 'REVIEW_BACK' && (
                        <span>
                          <CloseCircleOutlined style={{ color: 'red', marginRight: 5 }} /> 申请已驳回
                        </span>
                      )}
                      {dataDetails && dataDetails.applicationStatus === 'AGREEMENT_SIGNING' && (
                        <span>
                          <InfoCircleOutlined style={{ color: '#1890ff', marginRight: 5 }} /> 协议待签署
                        </span>
                      )}
                      {dataDetails && dataDetails.applicationStatus === 'BUSINESS_OPENING' && (
                        <span>
                          <InfoCircleOutlined style={{ color: '#1890ff', marginRight: 5 }} /> 业务开通中
                        </span>
                      )}
                      {dataDetails && dataDetails.applicationStatus === 'COMPLETED' && (
                        <span>
                          <CheckCircleTwoTone twoToneColor="#52c41a" style={{ marginRight: 5 }} /> 申请已完成（通过）
                        </span>
                      )}
                    </div>
                  </div>
                </div>
                {dataDetails && dataDetails.applicationStatus === 'REVIEW_BACK' && (
                  <div
                    style={{
                      width: 550,
                      margin: '0 auto',
                      textAlign: 'center',
                      marginTop: 30,
                      marginBottom: 50,
                    }}
                  >
                    <Button type="primary" onClick={() => this.props.history.goBack()}>
                      修改进件信息
                    </Button>
                  </div>
                )}
              </>
            )}
          </div>
        </div>
      </PageContainer>
    );
  }
}

export default connect()(NoticeNotice);
