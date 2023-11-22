//아이콘 + 상태 + 박스로 감싼 컴포넌트 (안전위험)
import { LuCheckCircle2, LuAlertCircle, LuHelpCircle } from "react-icons/lu";
import styled from "styled-components";

//Props 타입 정의
//SafeStatusProps: 컴포넌트가 받을 수 있는 props의 타입을 정의
//keyword는 선택적 속성
interface SafeStatusProps {
  keyword?: SafeKeyword;
}

//SafeKeyword 타입 정의
//keyword의 가능한 값을 "Safe", "Undefined", "Dangerous" 중 하나로 한정
type SafeKeyword = "Safe" | "Undefined" | "Dangerous";

//스타일 및 아이콘 설정 타입 정의
type SafeKeywordStyle = {
  Icon: React.ComponentType;
  text: string;
  color: string;
}

//SafeKeywordStyles: 각 SafeKeyword 값에 따른 스타일 및 아이콘을 매핑
const SafeKeywordStyles: Record<SafeKeyword, SafeKeywordStyle> = {
  Safe: {
    Icon: LuCheckCircle2,
    text: "안전",
    color: "#34B476"
  },
  Undefined: {
    Icon: LuAlertCircle,
    text: "미확인",
    color: "#C5C5C5"
  },
  Dangerous: {
    Icon: LuHelpCircle,
    text: "위험",
    color: "EF476F"
  },
};

//스타일링된 컴포넌트 정의
//styled-components를 사용하여 스타일링된 div 요소를 정의
//이 요소의 색상은 keyword prop에 따라 다름
    const StyledSafeStatusBox = styled.div<SafeStatusProps>`
      color: ${(props) => SafeKeywordStyles[props.keyword || "Undefined"].color};
      font-weight: bold;
    `;

//해당 컴포넌트는 상태(keyword)에 따른 텍스트와 아이콘을 렌더링
const SafeStatusBox = ({keyword}: SafeStatusProps) => {
  const {text, Icon } = SafeKeywordStyles[keyword || "Undefined"];

  return (
    <StyledSafeStatusBox keyword={keyword}>
      <span>{text}</span>
      <Icon />
    </StyledSafeStatusBox>
    );
};

export default SafeStatusBox