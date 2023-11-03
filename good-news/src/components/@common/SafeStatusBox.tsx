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

const StyledSafeStatusBox = styled.div<SafeStatusProps>`
  color: ${(props) => SafeKeywordStyles[props.keyword || "Undefined"].color};
  font-weight: bold;
`;

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