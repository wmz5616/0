import RightContent from '@/components/GlobalHeader/RightContent';
import { post } from '@/utils/request';
import { history, RunTimeLayoutConfig, useModel } from '@umijs/max';
// 运行时配置

// 全局初始化数据配置，用于 Layout 用户信息和权限初始化
// 更多信息见文档：https://umijs.org/docs/api/runtime-config#getinitialstate
// export async function getInitialState(): Promise<{ name: string }> {
//   return { name: '@umijs/max' };
// }
export async function getInitialState() {
  try {
    const ressd = await post('/guzhe/cas/info');
    return { permissions: ressd.data.ruleIds || [] };
  } catch (e) {
    history.push('/user/login');
    return { permissions: [] };
  }
  // 模拟从后端接口获取用户的权限列表或角色信息
  // const ressd = await post('/guzhe/cas/info');
  // return {
  //   permissions: ressd.data.ruleIds,
  // };
}

export const layout: RunTimeLayoutConfig = () => {
  const { fInfo } = useModel('global');
  console.log(fInfo);
  return {
    title: fInfo?.name,
    logo: fInfo?.logo,
    menu: {
      locale: true,
    },
    layout: 'mix',
    rightContentRender: () => <RightContent />,
    footerRender: () => {
      return (
        fInfo.version && (
          <div
            style={{
              textAlign: 'center',
              paddingBottom: '1%',
            }}
          >
            {/* Copyright © {new Date().getFullYear()} 东莞市松山湖体育管理服务中心 All Rights
              Reserved. &nbsp; */}
            {fInfo.version}&nbsp;&nbsp;
            <a
              style={{ color: 'rgba(0, 0, 0, 0.85)' }}
              href="https://beian.miit.gov.cn/#/Integrated/index"
            >
              {/* 粤ICP备2024201897号 */}
              {fInfo.miitbeian}
            </a>
            <div>
              技术支持：
              <a
                href="https://www.bluewise.cc/"
                target="_blank"
                rel="noreferrer"
                style={{ color: '#000' }}
              >
                东莞市蓝睿网络科技有限公司
              </a>
            </div>
          </div>
        )
      );
    },
  };
};
