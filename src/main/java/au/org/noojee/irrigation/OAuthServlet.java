package au.org.noojee.irrigation;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet(urlPatterns = "/oauth2", name = "OAuth2", asyncSupported = false)

public class OAuthServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
/*
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{

		try
		{
			// dynamically recognize an OAuth profile based on request characteristic (params,
			// method, content type etc.), perform validation
			OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);

			validateRedirectionURI(oauthRequest);

			OAuthIssuer oauthIssuerImpl;
			OAuthProblemException ex;
			// build OAuth response
			OAuthResponse resp = OAuthASResponse
					.authorizationResponse(request, HttpServletResponse.SC_FOUND)
					.setCode(oauthIssuerImpl.authorizationCode())
					.location(ex.getRedirectUri())
					.buildQueryMessage();

			response.sendRedirect(resp.getLocationUri());

			// if something goes wrong
		}
		catch (OAuthProblemException | OAuthSystemException ex)
		{
			String redirectUri;
			OAuthResponse resp;
			try
			{
				resp = OAuthASResponse
						.errorResponse(HttpServletResponse.SC_FOUND)
						.error(ex)
						.location(redirectUri)
						.buildQueryMessage();
			}
			catch (OAuthSystemException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			response.sendRedirect(resp.getLocationUri());
		}

	}

	private void validateRedirectionURI(OAuthAuthzRequest oauthRequest)
	{
		// TODO Auto-generated method stub

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{

		OAuthTokenRequest oauthRequest = null;

		OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

		try
		{
			oauthRequest = new OAuthTokenRequest(request);

			validateClient(oauthRequest);

			String authzCode = oauthRequest.getCode();

			// some code
			String accessToken = oauthIssuerImpl.accessToken();
			String refreshToken = oauthIssuerImpl.refreshToken();

			// some code
			OAuthResponse r = OAuthASResponse
					.tokenResponse(HttpServletResponse.SC_OK)
					.setAccessToken(accessToken)
					.setExpiresIn("3600")
					.setRefreshToken(refreshToken)
					.buildJSONMessage();

			response.setStatus(r.getResponseStatus());
			PrintWriter pw = response.getWriter();
			pw.print(r.getBody());
			pw.flush();
			pw.close();
			// if something goes wrong
		}
		catch (OAuthProblemException ex)
		{

			OAuthResponse r;
			try
			{
				r = OAuthResponse
						.errorResponse(401)
						.error(ex)
						.buildJSONMessage();
				response.setStatus(r.getResponseStatus());

				PrintWriter pw = response.getWriter();
				pw.print(r.getBody());
				pw.flush();
				pw.close();

				response.sendError(401);

			}
			catch (OAuthSystemException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		catch (OAuthSystemException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void

			validateClient(OAuthTokenRequest oauthRequest)
	{ // TODO Auto-generated method stub

	}
*/
}
