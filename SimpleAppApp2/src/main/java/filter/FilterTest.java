package filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;

/**
 * Servlet Filter implementation class FilterTest
 */
@WebFilter("/*")
public class FilterTest extends HttpFilter implements Filter {
   

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		//リクエスト前に実行
		request.setCharacterEncoding("UTF-8");
		//次のフィルターまたはサーブレット・JSPにリクエスト
		chain.doFilter(request, response);
	}

}
