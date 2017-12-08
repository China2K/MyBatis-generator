package com.iss.core.generate;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Base Controller
 */
public abstract class BaseController<T extends BaseEntity, DTO extends T, E extends AbstractExample<T>> {

	public static final String DEFAULT_JSON_DATA = "aaData";

	public static final String DEFAULT_JSON_TOTAL_PROPERTY = "iTotalRecords";

	public static final String DEFAULT_JSON_SECHO = "sEcho";

	public static final String DEFAULT_JSON_RECORDSFILTERED = "iTotalDisplayRecords";

	public static final String DEFAULT_JSON_MESSAGE = "message";

	public static final String DEFAULT_JSON_SUCCESS = "success";

	protected Class<T> entityClazz;
	protected Class<E> exampleClazz;

	@SuppressWarnings("unchecked")
	public BaseController() {
		ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
		entityClazz = (Class<T>) type.getActualTypeArguments()[0];
		exampleClazz = (Class<E>) type.getActualTypeArguments()[1];
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(true);
		binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true));
	}

	protected JSONObject toJSONResult(long count, List<?> data) {
		JSONObject result = new JSONObject();
		result.put(DEFAULT_JSON_SUCCESS, true);
		result.put(DEFAULT_JSON_TOTAL_PROPERTY, count);
		result.put(DEFAULT_JSON_DATA, data);
		return result;
	}

	protected JSONObject toJSONResult(long count, List<?> data, int sEcho) {
		JSONObject result = new JSONObject();
		result.put(DEFAULT_JSON_TOTAL_PROPERTY, count);
		result.put(DEFAULT_JSON_RECORDSFILTERED, count);
		result.put(DEFAULT_JSON_DATA, data);
		result.put(DEFAULT_JSON_SECHO, sEcho);
		return result;
	}

	protected JSONObject toJSONResult(boolean success, String message) {
		JSONObject result = new JSONObject();
		result.put(DEFAULT_JSON_SUCCESS, success);
		if (StringUtils.isNotBlank(message)) {
			result.put(DEFAULT_JSON_MESSAGE, message);
		}
		return result;
	}

	protected JSONObject toJSONResult(boolean success) {
		JSONObject result = new JSONObject();
		result.put(DEFAULT_JSON_SUCCESS, success);
		return result;
	}

	protected JSONObject toJSONResult(boolean success, Object data) {
		JSONObject result = new JSONObject();
		result.put(DEFAULT_JSON_SUCCESS, success);
		result.put(DEFAULT_JSON_DATA, data);
		return result;
	}

	protected Map<String, Object> toJSONResultMap(boolean success, Object data) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(DEFAULT_JSON_SUCCESS, success);
		result.put(DEFAULT_JSON_DATA, data);
		return result;
	}

	protected Map<String, Object> toJSONResultMap(boolean success, String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(DEFAULT_JSON_SUCCESS, success);
		result.put(DEFAULT_JSON_MESSAGE, msg);
		return result;
	}

	protected JSONArray toJSONArrayResult(List<?> data) {
		return JSONArray.fromObject(data);
	}

	protected Pageable convert2Pageable(int start, int limit) {
		if (limit == 0) {
			limit = Integer.MAX_VALUE;
		}
		Pageable pageable = new PageRequest(start / limit, limit);
		return pageable;
	}

	/**
	 * 根据前台的param参数，返回searchbean对象
	 * <p/>
	 * 
	 * @param param
	 */
	@Deprecated
	protected SearchBean[] convert2SearchBean(String param) {
		return getBaseService().convert2SearchBean(param);
	}

	protected List<SearchBean[]> convert2OrSearchBean(String param) {
		return getBaseService().convert2OrSearchBean(param);
	}

	@Deprecated
	protected E convert2Example(String param) {
		return getBaseService().convert2Example(param);
	}

	protected E convert2OrExample(String param) {
		return getBaseService().convert2OrExample(param);
	}

	@Deprecated
	protected E convertSearchBean2Example(SearchBean[] searchBeans) {
		return getBaseService().convertSearchBean2Example(searchBeans);
	}

	protected E convertSearchBean2OrExample(List<SearchBean[]> searchBeansAry) {
		return getBaseService().convertSearchBean2OrExample(searchBeansAry);
	}

	protected abstract IBaseService<T, DTO, E, ? extends Serializable> getBaseService();

	protected void setUserToCookie(String id, HttpServletRequest req, HttpServletResponse resp) {
		Cookie cookie = new Cookie("ui", id);
		cookie.setPath(req.getContextPath());
		cookie.setMaxAge(Integer.MAX_VALUE);
		resp.addCookie(cookie);
		try {
			resp.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void removeUserToCookie(HttpServletRequest req, HttpServletResponse resp) {
		Cookie cookie = new Cookie("ui", "");
		cookie.setPath(req.getContextPath());
		cookie.setMaxAge(Integer.MAX_VALUE);
		resp.addCookie(cookie);
		try {
			resp.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void doLogin(String id, HttpServletResponse resp, HttpServletRequest req, boolean addToCookie) {
		req.getSession().setAttribute(SESSION_USER, id);
		if (addToCookie) {
			setUserToCookie(id, req, resp);
		}
	}

	protected void doLogout(HttpServletResponse resp, HttpServletRequest req) {
		req.getSession().removeAttribute(SESSION_USER);
		removeUserToCookie(req, resp);
	}

	protected String getUserIdFromCookie(HttpServletRequest req) {
		String uid = "";
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("ui")) {
					if (StringUtils.isNotBlank(cookie.getValue())) {
						uid = cookie.getValue();
					}
					break;
				}
			}
		}
		return uid;
	}

	public static String getSessionUser(HttpServletRequest req) {
		return req.getSession().getAttribute(SESSION_USER) == null ? null : ((String) req.getSession().getAttribute(
				SESSION_USER));
	}

	public static final String SESSION_USER = "loginUser";
}
