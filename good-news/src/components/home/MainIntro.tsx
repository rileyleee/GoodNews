import Text from "../@common/Text";

// 메인페이지의 제일 처음 부분
const MainIntro = () => {
  return (
    <div>
      메인페이지
      <div>폰트크기 테스트</div>
      <div style={{ fontSize: "16px" }}>폰트크기 테스트 16px - 설명문이나 footer 본문</div>
      <div style={{ fontSize: "20px" }}>폰트크기 테스트 20px</div>
      <div style={{ fontSize: "24px" }}>폰트크기 테스트 24px</div>
      <div style={{ fontSize: "32px" }}>폰트크기 테스트 32px</div>
      <div style={{ fontSize: "36px" }}>폰트크기 테스트 36px</div>
      <div style={{ fontSize: "40px" }}>폰트크기 테스트 40px</div>
      <Text size="text1" isBold={true}>안녕하세요?</Text>
      <Text size="text1" >안녕하세요?</Text>
    </div>
  );
};

export default MainIntro;
